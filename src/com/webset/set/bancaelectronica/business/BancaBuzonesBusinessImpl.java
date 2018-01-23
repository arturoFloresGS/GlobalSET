package com.webset.set.bancaelectronica.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dao.BancaBuzonesDao;
import com.webset.set.bancaelectronica.dto.EmpresaDto;
import com.webset.set.bancaelectronica.dto.MovtoBancaEDto;
import com.webset.set.bancaelectronica.service.BancaBuzonesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFilenameFilter;

/**
 * 
 * @author Eric Medina
 * @since 24/11/2015
 * @see Clase que se ejecuta por medio de un proceso cron, es una migraci�n del scheduler bancaBuzones de VB.
 * 
 */

public class BancaBuzonesBusinessImpl implements BancaBuzonesService{

	private BancaBuzonesDao objBancaBuzonesDao;
	private Bitacora bitacora = new Bitacora(); 
	private SmbFile creaArchivo = null;
	private Funciones funciones = new Funciones();
	private String archivos;
	private String chequerasInexistentes;
	private String fechaHoy;
	private int idUsuario;
	
	/***************  CONSTANTES	*****************/
	//public static final int BANAMEX = 2;
	public static final int BANCOMER = 12;
	private static final int SANTANDER = 14;
	private static final int AZTECA = 127;
	/*private static final int BITAL = 21;
	private static final int INBURSA = 36;
	private static final int BITAL_DLS = 1229;
	private static final int AMRO_BANK = 102;
	private static final int INVERLAT = 44; //SCOTIABANK
	private static final int BANORTE = 72;
	//BANCOS Extranjeros
	private static final int BANK_BOSTON = 107;
	private static final int COMERICA = 1260;
	private static final int CITIBANK_DLS = 1026;
	private static final int FUBI = 1234; //FIRST UNION NATIONAL BANK
	private static final int AMEX = 1980; //AMERICAN EXPRESS
	private static final int NAFIN = 135; //NACIONAL FINANCIERA
	private static final int BOFA = 1027; //BANK OF AMERICA
	private static final int SANT_MADRID = 10605;  //BANK OF AMERICA SANTANDER MADRID ESPA�A
	*/	
	
	/**********************************************************************/
	public BancaBuzonesDao getObjBancaBuzonesDao() {
		return objBancaBuzonesDao;
	}

	public void setObjBancaBuzonesDao(BancaBuzonesDao bancaBuzonesDao) {
		this.objBancaBuzonesDao = bancaBuzonesDao;
	}
	/**********************************************************************/

	//Este m�todo se ejecuta a trav�s de un proceso cron.
	public void leerArchivosBanca(){
		
		fechaHoy = funciones.ponerFechaSola(new Date());
		idUsuario = 1;
		
		boolean confirmar = false;
		boolean banBanamex = false;
		
		//No se necesita ruta regreso para la configuracion de archivos en smb, pendiente a borrar
		/*String valorConfiguraSet = objBancaBuzonesDao.consultarConfiguraSet(2456);
		String rutaRegreso = "";
		if(!valorConfiguraSet.equals("")){
			rutaRegreso = valorConfiguraSet;
		}else{
			//No hay ruta de regreso, termina programa.
			return;
		}*/
		
		//*********	SANTANDER	**********
		archivos = "";
		chequerasInexistentes = "";
		/*** Variables no se para que se utilizan ***/
		long regLeidos = 0;
		long regSinChequera = 0;
		long regImportados = 0;
		//String rutaFiles = obtenerRutaBusquedaArchivosMH(rutaRegreso); 

		if(leeSantanderTotal(archivos, chequerasInexistentes, regLeidos, regSinChequera, regImportados, "")){
			actualizarFechaBanca(archivos, 14); 
			confirmar = true;
		}else{
			//System.out.println("Hubo algun error o no termino de leer todos las lineas de un archivo");
		}
			
		archivos = "";
		chequerasInexistentes = "";
		regLeidos = 0;
		regSinChequera = 0;
		regImportados = 0;
		//rutaFiles = obtenerRutaBusquedaArchivosMD(rutaRegreso); 
		
		if(leeSantanderTotalMD(archivos, chequerasInexistentes, regLeidos, regSinChequera, regImportados, "")){
			//System.out.println("Completo leerSantander exitosamente.");
			actualizarFechaBanca(archivos, 14); //revisar archivos xq manda vacio.
			confirmar = true;
		}else{
			//System.out.println("Hubo algun error o no termino de leer todos las lineas de un archivo");
		}
		
		//SIGUEN LOS DEMAS BANCOS
		banBanamex = true;
		
		/**********************************************************************************/
		/********************************	BANCOMER	***********************************/
        /**********************************************************************************/
        
		if(leeNetCashC43(archivos, chequerasInexistentes, regLeidos, regSinChequera, regImportados)){
			actualizarFechaBanca(archivos, 12);
			confirmar = true;
		}
		
		//CODIGO PARA LECTURA DE ARCHIVOS CON LAYOUT CUADERNO 43 MISMO DIA
        archivos = "";
        chequerasInexistentes = "";
        regLeidos = 0;
        regSinChequera = 0;
        regImportados = 0;
        regLeidos = 0;
        
        if(leeNetCashC43Diario(archivos, chequerasInexistentes, regLeidos, regSinChequera, regImportados)){
        	actualizarFechaBanca(archivos, 12);
        	confirmar = true;
        }

        
        /**********************************************************************************/
		/********************************	  AZTECA	***********************************/
        /**********************************************************************************/
        
        archivos = "";
		chequerasInexistentes = "";
		/*** Variables no se para que se utilizan ***/
		regLeidos = 0;
		regSinChequera = 0;
		regImportados = 0;

		if(leeBancoAzteca()){
			actualizarFechaBanca(archivos, 127); 
			confirmar = true;
		}else{
			//System.out.println("Hubo algun error o no termino de leer todos las lineas de un archivo");
		}
		
		
		if(leeBancoAztecaMd()){
			actualizarFechaBanca(archivos, 127);
			confirmar = true;
		}
        /*****		Hasta aqui lectura de bancos, sigue ejecucion de stores.	*****/
        //if(true) return;
        /*****	*****	*****	*****	*****	*****	*****	*****	*****	*****/
        
		//Descomentado 20/04/2016
		Map<String, Object> map = new HashMap<>();
		map = objBancaBuzonesDao.ejecutaConfOperaciones();
		
	   /*********************** 	EJECUTA EL ESTORE DE COBRANZA CIE 	***************************
	    ***	AQUI GENERA EL ARCHIVO DE CONBRANZA POR SI HAY ALGUN ERROR EN LOS DE MAS PROCESOS	***
	    *** NO OBSTRUYA LA GENERACION DEL ARCHIVO												***	
	    *******************************************************************************************/
	   

		if(banBanamex){
			//Call Reparar_referencias_banamex
		}
	    confirmar = true;  
		if(confirmar){
			//confirmaTransfer(); //confirma las transferencias en forma automatica - Llama al stored confirmador.
			
			/*****	SE COMENTA YA QUE ERA NO SE NECESITA LA CONSULTA DE CONTROL_STORED	*/
			//Descomentado el 20/04/2016
			String estatusClasificador = "";
			estatusClasificador = objBancaBuzonesDao.selectControlStored("SP_CLASIFICADORMOVTOS", 2);
			
			if(!estatusClasificador.equals("1")){
				objBancaBuzonesDao.ejecutaClasificadorMovtos(fechaHoy);

				//Saca llamada de los stores que apuntan a movimiento.
				objBancaBuzonesDao.ejecutaBuzonesMovimiento();
			}
			
			objBancaBuzonesDao.ejecutaClasificadorMovtos(fechaHoy);

			//Saca llamada de los stores que apuntan a movimiento.
			objBancaBuzonesDao.ejecutaBuzonesMovimiento(); 

			
			//STORES PENDIENTES
			//objBancaBuzonesDao.ejecutaConciliaSbcDev(); //sp_concilia_sbc_dev
			//objBancaBuzonesDao.ejecutaDatosDevolucionCheque(); //sp_Datos_Devolucion_Cheque
			//objBancaBuzonesDao.ejecutaFactorajeBancomer(); //sp_Factoraje_Bancomer
			 	            
		}
		
		/******	LLAMADA A CONTROL_STORED	*****/
		//++++ Descomentado 20/04/2016	+++++//
		String controlStored = objBancaBuzonesDao.selectControlStored("sp_concilia_banco_movto", 1); 
		
		if(!controlStored.equals("1")){
			objBancaBuzonesDao.ejecutaStoreSPC(idUsuario);		
		}
		
		objBancaBuzonesDao.ejecutaStoreSPC(idUsuario); 	
		//+++++ 						++++//
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			bitacora.insertarRegistro(new Date().toString() + " Error: " + " Thread.sleep"
					+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leerArchivosBanca");	
		}

	}

	public boolean leeSantanderTotal(String archivos, String chequerasInexistentes, long regLeidos,
			long regSinChequera, long regImportados, String rutaFiles) { //rutaFiles No se ocupa.
		System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
		//variables
		boolean leeSantander;
		boolean archivOk;
		String archivo;
		String fecha;
	    int noEmpresa;
	    String sbCobro;
	    String registro;
	    String chequera;
		long folio;
		int contador;
		String sucursal;
		String folioBanco;
		String referencia;
		String cargoAbono;
		double importe;
		String concepto;
		String descripcion;
		double saldo;
		String cveConcepto;
		String estatus;
		//fin variables
		
		//Inicializa variables
		leeSantander = false;
		contador = 0;
		cveConcepto = "";
		estatus = "S";
		archivo = "";
		registro = "";
		sbCobro = "";
		chequera = "";
		
		List<String> valoresConexion =  objBancaBuzonesDao.armaCadenaConexion();
		
		String dirPrincipal = "";
		
		if (valoresConexion.size()!=0 ) {
    		dirPrincipal = "smb://"+valoresConexion.get(0)+":"+valoresConexion.get(1)+
	    			"@"+valoresConexion.get(2)+"/"+valoresConexion.get(3);
    		
    		System.out.println("dirPrincipal: "+dirPrincipal);
    		
    		Map<String, Object> mapa = crearDirectorioLayout(dirPrincipal, "/santander/EDO_CUENTA/");
    		
    		if((boolean) mapa.get("resExito")){
    			//Obtiene todos los archivos que contenga el directorio y que se van a leer.
    			SmbFile archivosSmb[] = obtenerArchivosDirectorio(dirPrincipal+"/santander/EDO_CUENTA/");
    			
    			if(archivosSmb.length > 0){
    				
    				for(SmbFile smbFile: archivosSmb){
    					
    					if(renombrarArchivo(smbFile, "tmp", false)){
    					
    						
					        archivOk = true;
					       
					        archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					        archivos = setStringFiles(archivos, archivo);
					        //archivo = la direccion completa donde se encientra el archivo
					        archivo = stringFileWithExtTmp(smbFile.getCanonicalPath());

					        noEmpresa = 0;
					        
					        try {
			        			
					        	BufferedReader reader = null;
					        	reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(archivo)));
					        	//Quitamos la direccion absoluta y solo dejamos el nombre del archivo con tmp.
					        	archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					        	
					  		  	String lineReader = null;
					  		  	
					  		  try{
					  		    
					  			  //Lee una linea
					  			  while ((lineReader = reader.readLine()) != null) {
					  				  
					  				  registro = lineReader;
					  				
					  				  contador += 1;
				  			          regLeidos = contador;
				  			          fecha = getDateOfRecord(registro);
				  			                    
				  			          if(!funciones.isDate(fecha, false)){
				  			        	  archivOk = false;
				  			        	  break;
				  			          }
				                        
				  			          //transaccion = true;
				  	                        
			  			              chequera = getChequeraOfRecord(registro);
			  			              
			  			              List<EmpresaDto> empresas = new ArrayList<>();
			  			              
			  			              empresas = objBancaBuzonesDao.buscarEmpresa(chequera, SANTANDER, false);
			  			              
			  			              //consulta de chequera 
			  			              if(empresas.size() > 0){
			  			            	  noEmpresa = empresas.get(0).getNoEmpresa();
			  			              }else{
			  			            	  regSinChequera += 1;
			  			            	  if(chequerasInexistentes.indexOf(chequera) < 0){
			  			            		  chequerasInexistentes = chequerasInexistentes + chequera + "\n";
			  			            	  }
			  			            	  
			  			            	  //Siguiente linea.
			  			            	  continue;
			  			              }
			  			              
			  			              sbCobro = getSBC(registro);
			  			              sucursal = registro.substring(28, 32); //29,4
			  			              folioBanco = registro.substring(105, 113); //106,8
			  			              cargoAbono = (registro.substring(76,77).equals("+")?"I":"E"); //77,1
			  			              referencia = registro.substring(113,121).trim(); //114,8
			  			              if(cargoAbono.equals("E")){
			  			            	  referencia = registro.substring(113,133); //114,20
			  			              }else{
			  			            	  referencia = registro.substring(113,143); //114,30
			  			              }
			  			              
			  			              importe = Double.parseDouble(registro.substring(77,91))/100;  //78,14
			  			              concepto = registro.substring(36, 76).trim(); //37,40
			  			              if(registro.substring(76,77).equals("-")){ //77,1
			  			            	  descripcion = registro.substring(110,113) + "-" + registro.substring(105,110); //111,3 - 106,5
			  			              }else{
			  			            	  descripcion = registro.substring(105,113); //1006,8
			  			              }
			  			                  
			  			               saldo = Double.parseDouble(registro.substring(91, 105))/100; //92,14
			  			               cveConcepto = registro.substring(32,36).trim(); //33,4
			  			               if(cveConcepto.equals("0000")){
			  			            	   cveConcepto = "";
			  			               }
			  			                
			  			               //Tal vez hay que convertir la decha de la instancia a string.
			  			               List<MovtoBancaEDto> movimientos = new ArrayList<>();
			  			               
			  			               Map<String, Object> mapa2 = new HashMap<>();
			  			               
			  			               if(fecha.equals(fechaHoy)){
			  			            	    mapa2 = objBancaBuzonesDao.selectMD(folioBanco, concepto, referencia, noEmpresa, chequera, fecha, importe, descripcion);
			  			               }else{
			  			            	  mapa2 = objBancaBuzonesDao.selectMovtoBancaE(folioBanco, concepto, referencia, noEmpresa, chequera, fecha, importe, saldo, 0, "", 0 );
			  			               }
			  			               
			  			               movimientos = (List<MovtoBancaEDto>) mapa2.get("listaMovto");
			  			               
			  			               if(movimientos.size() > 0){
			  			            	   continue; // Siguiente linea
			  			               }
			  			               
			  			               
			  			              //sacamos el siguiente folio de la base de datos
			  			               folio = folioReal("secuencia");
			  			               
			  			               leeSantander = true; //Si llega aqui se retornara como exitoso
			  			               
			  			             //**********************************************************************
			  			             //Inicia para no cargar dias anteriores a ayer
			  			            //importa = 0;
			  			                           
			  			          if(fecha.equals(fechaHoy)){
			  			        	  mapa2 = objBancaBuzonesDao.insertaBancaMD(noEmpresa, SANTANDER, chequera, folio, fecha, sucursal, folioBanco, 
			  			        			  									referencia, cargoAbono, importe, sbCobro, concepto, idUsuario,
			  			        			  									descripcion, archivo, fechaHoy, 
			  			        			  									"",cveConcepto, saldo, "", 0);
			  			        	  
		  			            	  //falta aqui abajo		  			            	  
		  			               }else{
		  			            	  mapa2 = objBancaBuzonesDao.insertaMovtoBancaE(noEmpresa, SANTANDER, chequera, folio, fecha, sucursal, folioBanco, 
			  									referencia, cargoAbono, importe, sbCobro, concepto,idUsuario,
			  									descripcion, archivo, fechaHoy, 
			  									"",cveConcepto, saldo, estatus, 0);
		  			               }
			  			          
			  			          regImportados += 1;

					  			  }//fin while lector de lineas archivos
					  			  
					  			  if(archivOk){
					  				  renombrarArchivo(new SmbFile(stringFileWithExtTmp(smbFile.getCanonicalPath())), "old", true);
					  				  objBancaBuzonesDao.insertaArchMovtoBancaE(smbFile.getName(), chequera.substring(chequera.length()-8 , chequera.length()), fechaHoy, SANTANDER);
					  			  }
					  		        
					  		   }catch (IOException exception) {
					  			   bitacora.insertarRegistro(new Date().toString() + " " + exception.toString()+ " "
				    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
					  		   }finally {
					  			   try {
					  				   reader.close();
					  			   } catch (IOException ex) {
					  				 bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()+ " "
					    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
					  			   }
					  		   }
					  		  
			        			
			        		}catch(Exception e){
			        			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ " "
		    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
			        		}
					        
    					}
    					
    				}//fin foreach
    				
    				this.archivos = archivos;
    				this.chequerasInexistentes = chequerasInexistentes;
    				
    				return leeSantander;
    			}else{
    				 bitacora.insertarRegistro(new Date().toString() + " " + "No hay archivos a procesar en el directorio."
						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
    				return false;
    			}
    			
    		}else{
    			 bitacora.insertarRegistro(new Date().toString() + " " + String.valueOf(mapa.get("msgUsuario"))
    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
    			return false;
    		}
    		
		}else {
			 bitacora.insertarRegistro(new Date().toString() + " " + "msgUsuario: No se encontraron las variantes para generar la ruta"
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
		}
		
		return false;
	}

	public boolean leeSantanderTotalMD(String archivos, String chequerasInexistentes, long regLeidos,
			long regSinChequera, long regImportados, String rutaFiles) {
		//variables
		boolean leeSantanderMD;
		boolean archivOk;
		String archivo;
		String fecha;
	    int noEmpresa;
	    String sbCobro;
	    String registro;
	    String chequera;
		long folio;
		int contador;
		String sucursal;
		String folioBanco;
		String referencia;
		String cargoAbono;
		double importe;
		String concepto;
		String descripcion;
		double saldo;
		String cveConcepto;
		//String estatus;
		//fin variables
		
		//Inicializa variables
		leeSantanderMD = false;
		contador = 0;
		cveConcepto = "";
		archivo = "";
		registro = "";
		sbCobro = "";
		chequera = "";
		
		System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
		
		List<String> valoresConexion =  objBancaBuzonesDao.armaCadenaConexion();
		
		String dirPrincipal = "";
		
		if (valoresConexion.size()!=0 ) {
    		dirPrincipal = "smb://"+valoresConexion.get(0)+":"+valoresConexion.get(1)+
	    			"@"+valoresConexion.get(2)+"/"+valoresConexion.get(3);
    		
    		Map<String, Object> mapa = crearDirectorioLayout(dirPrincipal, "/santander/MismoDia/");
    		
    		if((boolean) mapa.get("resExito")){
    			//Obtiene todos los archivos que contenga el directorio y que se van a leer.
    			SmbFile archivosSmb[] = obtenerArchivosDirectorio(dirPrincipal+"/santander/MismoDia/");
    			
    			if(archivosSmb.length > 0){
    				
    				for(SmbFile smbFile: archivosSmb){
    					
    					if(renombrarArchivo(smbFile, "tmp", false)){
    					
    						archivOk = true;
  					       
 					        archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
 					        archivos = setStringFiles(archivos, archivo);
 					        //archivo = la direccion completa donde se encientra el archivo
 					        archivo = stringFileWithExtTmp(smbFile.getCanonicalPath());

					        noEmpresa = 0;
					        
					        try {
			        			
					        	BufferedReader reader = null;
					        	reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(archivo)));
					        	archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					        	
					        	String lineReader = null;
					  		  	
					  		  try{
					  		    
					  			  //Lee una linea, pero da los registros ya separados por el salto de linea, se evita cortar cadenas por salto de lineas.
					  			  while ((lineReader = reader.readLine()) != null) {
					  				  
 					  				  registro = lineReader;

					  				  contador += 1;
				  			          regLeidos = contador;
				  			          fecha = getDateOfRecord(registro);
				  			                    
				  			          if(!funciones.isDate(fecha, false)){
				  			        	  archivOk = false;
				  			        	  break;
				  			          }
				                        
				  			          //transaccion = true;
				  	                        
			  			              chequera = getChequeraOfRecord(registro);
			  			              
			  			              List<EmpresaDto> empresas = new ArrayList<>();
			  			              
			  			              empresas = objBancaBuzonesDao.buscarEmpresa(chequera, SANTANDER, false);
			  			              
			  			              //consulta de chequera 
			  			              if(empresas.size() > 0){
			  			            	  noEmpresa = empresas.get(0).getNoEmpresa();
			  			              }else{
			  			            	  regSinChequera += 1;
			  			            	  if(chequerasInexistentes.indexOf(chequera) < 0){
			  			            		  chequerasInexistentes = chequerasInexistentes + chequera + "\n";
			  			            	  }

			  			            	  //Siguiente linea.
			  			            	  continue;
			  			              }
			  			              
			  			              sbCobro = getSBC(registro);
			  			              sucursal = registro.substring(28, 32); //29,4
			  			              folioBanco = registro.substring(105, 113); //106,8
			  			              cargoAbono = (registro.substring(76,77).equals("+")?"I":"E"); //77,1
			  			              referencia = registro.substring(113,121).trim(); //114,8
			  			              if(cargoAbono.equals("E")){
			  			            	  referencia = registro.substring(113,133); //114,20
			  			              }else{
			  			            	  referencia = registro.substring(113,143); //114,30
			  			              }
			  			              
			  			              importe = Double.parseDouble(registro.substring(77,91))/100;  //78,14
			  			              concepto = registro.substring(36, 76).trim(); //37,40
			  			              descripcion= concepto;
			  			            
			  			              /*if(registro.substring(76,77).equals("-")){ //77,1
			  			            	  descripcion = registro.substring(110,113) + "-" + registro.substring(105,110); //111,3 - 106,5
			  			              }else{
			  			            	  descripcion = registro.substring(105,113); //1006,8
			  			              }*/
			  			                  
			  			               saldo = Double.parseDouble(registro.substring(91, 105))/100; //92,14
			  			               cveConcepto = registro.substring(32,36).trim(); //33,4
			  			               if(cveConcepto.equals("0000")){
			  			            	   cveConcepto = "";
			  			               }
			  			                
			  			               //Tal vez hay que convertir la fecha de la instancia a string.
			  			               List<MovtoBancaEDto> movimientos = new ArrayList<>();
			  			               
			  			               Map<String, Object> mapa2 = new HashMap<>();
			  			               
			  			               if(fecha.equals(fechaHoy)){
			  			            	    mapa2 = objBancaBuzonesDao.selectMD(folioBanco, concepto, referencia, noEmpresa, chequera, fecha, importe, descripcion);
			  			               }
			  			               
			  			               movimientos = (List<MovtoBancaEDto>) mapa2.get("listaMovto");
			  			               if(movimientos.size() > 0){
			  			            	   continue; // Siguiente registro
			  			               }
			  			               
			  			               
			  			              //sacamos el siguiente folio de la base de datos
			  			               folio = folioReal("secuencia");
			  			               
			  			               leeSantanderMD = true; //Si llega aqui se retornara como exitoso
			  			               
			  			             //**********************************************************************
			  			             //Inicia para no cargar dias anteriores a ayer
			  			            //importa = 0;
			  			                           
			  			          if(fecha.equals(fechaHoy)){
			  			        	  mapa2 = objBancaBuzonesDao.insertaBancaMD(noEmpresa, SANTANDER, chequera, folio, fecha, sucursal, folioBanco, 
			  			        			  									referencia, cargoAbono, importe, sbCobro, concepto, idUsuario,
			  			        			  									descripcion, archivo,fechaHoy, 
			  			        			  									"",cveConcepto, saldo, "", 0);

			  			        	regImportados += 1;
			  			        	
		  			               }
			  			          
					  			  }//fin while lector de lineas archivos
					  			  
					  			  if(archivOk){
					  				renombrarArchivo(new SmbFile(stringFileWithExtTmp(smbFile.getCanonicalPath())), "old", true);
					  				objBancaBuzonesDao.insertaArchMovtoBancaE(smbFile.getName(), chequera.substring(chequera.length()-8 , chequera.length()), fechaHoy, SANTANDER);
					  			  }
					  		        
					  		   }catch (IOException exception) {
					  			   bitacora.insertarRegistro(new Date().toString() + " " + exception.toString()+ " "
				    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotalMD");
					  		   }finally {
					  			   try {
					  				   reader.close();
					  			   } catch (IOException ex) {
					  				 bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()+ " "
					    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotalMD");
					  			   }
					  		   }
					  		  
			        			
			        		}catch(Exception e){
			        			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ " "
		    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotalMD");
			        		}
					        
    					}
    					
    				}//fin foreach
    				
    				return leeSantanderMD;
    			}else{
    				 bitacora.insertarRegistro(new Date().toString() + " " + "No hay archivos a procesar en el directorio."
						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
    				return false;
    			}
    			
    		}else{
    			 bitacora.insertarRegistro(new Date().toString() + " " + String.valueOf(mapa.get("msgUsuario"))
    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
    			return false;
    		}
    		
		}else {
			 bitacora.insertarRegistro(new Date().toString() + " " + "msgUsuario: No se encontraron las variantes para generar la ruta"
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeSantanderTotal");
		}
		
		return false;
	}
	
	/*public StringBuilder leerContenidoArchivo(SmbFile sFile, StringBuilder builder){
		  BufferedReader reader = null;
		  try {
			  reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(sFile)));
		  } catch(Exception e){
		  
		  }
		  
		  String lineReader = null;
		  
		  try {
		    
			  //Lee una linea
			  while ((lineReader = reader.readLine()) != null) {
				  builder.append(lineReader).append("\n");
			  }
		    
		   }catch (IOException exception) {
			   bitacora.insertarRegistro(new Date().toString() + " " + exception.toString()
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leerContenidoArchivo");
		   }finally {
			   try {
				   reader.close();
			   } catch (IOException ex) {
				   bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()
					+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leerContenidoArchivo");
			   }
		  }
		  return builder;
	}*/
	
	public String obtenerRutaBusquedaArchivosMH(String rutaGenerica) {
		
		try {
			return  (rutaGenerica + "\\santander\\edocuenta\\");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:obtenerRutaBusquedaArchivosMH");
		}
		
		return "";
	}
	
	public String obtenerRutaBusquedaArchivosMD(String rutaGenerica) {
		
		try {
			return  (rutaGenerica + "\\santander\\MismoDia\\");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:obtenerRutaBusquedaArchivosMD");
		}
		
		return "";
	}
	
	private Map<String, Object> crearDirectorioLayout(String dirPrincipal,String subCarpetas){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("resExito", false);
		result.put("msgUsuario", "Error desconocido.");
		try {
			System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
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
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:crearDirectorioLayout" +"No se creo el Directorio de la Bitacora " );
		}catch (SmbException e) {
			result.put("msgUsuario", "Error al tratar de crear el archivo.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:crearDirectorioLayout" +"No se creo el Directorio de la Bitacora " );
		}
		return result;
	}
	
	private SmbFile[] obtenerArchivosDirectorio(String ruta){
		System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
		SmbFile archivos[] = null;
				
		try {
			SmbFile directorio = new SmbFile(ruta);
			archivos = directorio.listFiles(new SmbFilenameFilter() {
				
				//Filtro para que solo retorne los archivos que terminan con txt, en una ruta especificada.
				@Override
				public boolean accept(SmbFile arg0, String arg1) throws SmbException {
					return arg1.endsWith(".txt");
				}
			});
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:obtenerArchivosDirectorio");
		}
		
		return archivos;
	}
	
	private boolean renombrarArchivo(SmbFile smbFile, String extensionArchivo, boolean fecha){
		
		try {
			System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
			//Renombrar
			if(fecha){
				String fechaTemp = fechaHoy.replace("/", "");
				smbFile.renameTo(new SmbFile(smbFile.getCanonicalPath().substring(0, smbFile.getCanonicalPath().length() - 4 )+ fechaTemp +"."+ extensionArchivo));
			}else{
				smbFile.renameTo(new SmbFile(smbFile.getCanonicalPath().substring(0, smbFile.getCanonicalPath().length() - 3 )+ extensionArchivo));
			}
			
			return true;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:renombraTmp");
		}
		   /*Name archivo As Mid$(archivo, 1, Len(archivo) - 3) & "tmp"
		   renombraTmp = True
		*/
		return false;
	}

	/*private boolean renombraTmp(SmbFile smbFile){
		
		try {
			//Renombrar
			smbFile.renameTo(new SmbFile(smbFile.getCanonicalPath().substring(0, smbFile.getCanonicalPath().length() - 3 )+"tmp"));
			return true;
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:renombraTmp");
		}
		   //Name archivo As Mid$(archivo, 1, Len(archivo) - 3) & "tmp"
		   //renombraTmp = True
		
		return false;
	}*/
	
	private String setStringFiles(String total, String fileToAdd){
		String res = "";
		
		try{
			res = total + "'" + fileToAdd + "',";
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:setStringFiles");
		}
		return res;
	}
	
	private String stringFileWithExtTmp(String archivo){
	
		String res = "";
		try{
			res = archivo.substring(0,archivo.length() - 3) + "tmp"; 
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:stringFileWithExtTmp");
		}
		return res;
	}
	
	private String getDateOfRecord(String registro){
		String res = "";
		try{
			if(!registro.equals("")){
				res = registro.substring(18, 20) + "/" + registro.substring(16,18) + "/"+ registro.substring(20, 24);
			}
			//funGetDateOfRecord = Mid$(paramRegistro, 19, 2) & "/" & Mid$(paramRegistro, 17, 2) & "/" & Mid$(paramRegistro, 21, 4)
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:getDateOfRecord");
		}
		
		return res;
	}
	
	private String getChequeraOfRecord(String registro){
		String res = "";
		try{
			res = registro.substring(0,11);
			//funGetChequeraOfRecord = Format$(Mid$(paramRegistro, 1, 11), "################")
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:getChequeraOfRecord");
		}
		
		return res;
	}
    
	//Obtiene salvo buen cobro
	private String getSBC(String registro){
		String res = "";
		try{
			if(registro.substring(76,77).equals("+")){
				if(registro.substring(39,72).equals("DEP S B COBRO")){
					 res = "N";
				}else{
					res = "S";
				}
			}
			
			/* If Mid$(paramRegistro, 77, 1) = "+" Then
	           If Trim$(Mid$(paramRegistro, 40, 33)) = "DEP S B COBRO" Then */
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:getSBC");
		}
		
		return res;
	}
	
	
	private long folioReal(String idFolio){
		long folio = 0;
		Map<String, Object> mapa = new HashMap<>();
		
		try{
			mapa = objBancaBuzonesDao.updateFolioReal(idFolio.toLowerCase());
			
			if((int) mapa.get("afec") >= 1){
				mapa = objBancaBuzonesDao.selectFolioReal(idFolio.toLowerCase());
				if(!mapa.get("folio").equals("")){
					folio = (long)mapa.get("folio");
				}else if(!String.valueOf(mapa.get("estatus")).equals("")){
					bitacora.insertarRegistro(new Date().toString() + " " + mapa.get("estatus")
					+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:folioReal");
				}
			}else{
				bitacora.insertarRegistro(new Date().toString() + " " + mapa.get("estatus")
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:folioReal");
				
			}
			
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:folioReal");
		}
		
		return folio;
	}
	
	private void actualizarFechaBanca(String archivos, int idBanco){
		String fecha = fechaHoy;
		if(!archivos.equals("")){
			archivos = archivos.substring(0,archivos.length()-1);
			objBancaBuzonesDao.updateCatCtaBanco(fecha, archivos, idBanco); 
		}
	}

	private void confirmaTransfer(){
		
		double resp = 0;
		int intentos = 0;
		String valor = "";
		try {
			do{
				valor = objBancaBuzonesDao.consultarConfiguraSet(1);
				if(!valor.equals("") && !valor.trim().equals("CCM")){
					resp = confirmaTrans(); //Se quitaron los parametros de usuario y pass, ya que al irse al DAO ya tengo la conexion para ejecutar el stored.
				}	
				
				if(resp != 0){
					intentos += 1;
					
					if(intentos < 5){
						Thread.sleep(9000);
						
					}else{
						//insertar bitacora
						bitacora.insertarRegistro(new Date().toString() + " Error: " + resp  
						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:confirmaTransfer");
						break;
					}
				}else{
					break;
				}
				
			}while(intentos<5);
				
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:confirmaTransfer");
		}
	}
	
	
	private double confirmaTrans(){
		
		try{
			//Validar el retorno.
			return objBancaBuzonesDao.llamaProcConfirmaTrans();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:confirmaTrans");
		}
		
		return 0;
	}	

	
	private boolean leeNetCashC43(String archivos, String chequerasInexistentes, long regLeidos,
								  long regSinChequeras, long regImportados){
		
		boolean leeNetCashC43;
		boolean archivOk;
		String archivo;
		String fecha;
	    int noEmpresa;
	    String sbCobro;
	    String registro;
	    String chequera;
		long folio;
		int contador;
		String sucursal;
		String folioBanco;
		String referencia;
		String cargoAbono;
		double importe;
		String concepto;
		String descripcion;
		String cveConcepto;
		String estatus;
		String fechaSaldo;
		String chequeraNueva;
		String saldoBanco;
		int encontrados;
		double saldoChequera; 
		String observacion;
		int I = 0;
		List<MovtoBancaEDto> lMovimientos = new ArrayList<>();
		//fin variables
		
		//Inicializa variables
		leeNetCashC43 = false;
		contador = 0;
		cveConcepto = "";
		estatus = "S";
		archivo = "";
		registro = "";
		sbCobro = "";
		regLeidos = 0;
		concepto = "";
		fecha = "";
		importe = 0;
		cargoAbono = "";
		sucursal = "";
		observacion = "";
		encontrados = 0;
		fechaSaldo = "";
		saldoBanco = "";
		
		List<String> valoresConexion =  objBancaBuzonesDao.armaCadenaConexion();
		System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
		String dirPrincipal = "";
		
		if (valoresConexion.size()!=0 ) {
    		dirPrincipal = "smb://"+valoresConexion.get(0)+":"+valoresConexion.get(1)+
	    			"@"+valoresConexion.get(2)+"/"+valoresConexion.get(3);
    		
    		System.out.println("dirPrincipal: "+dirPrincipal);
    		
    		Map<String, Object> mapa = crearDirectorioLayout(dirPrincipal, "/bancomer/netcashC43/");
    		
    		if((boolean) mapa.get("resExito")){
    			//Obtiene todos los archivos que contenga el directorio y que se van a leer .txt.
    			SmbFile archivosSmb[] = obtenerArchivosDirectorio(dirPrincipal+"/bancomer/netcashC43/");    			
   
    			Map<String, Object> map = new HashMap<>();
    			
    			map = objBancaBuzonesDao.verificaSaldoBanco(12); //Buscar saldo banco
    			
    			if(!String.valueOf(map.get("saldo")).equals("")){
    				saldoBanco = String.valueOf(map.get("saldo"));
    			}
    			
    			if(archivosSmb.length > 0){
    				
    				for(SmbFile smbFile: archivosSmb){
    					
    					if(renombrarArchivo(smbFile, "tmp", false)){
    						
    						chequera = "";
    				        contador = 0;
    				        archivOk = true;
    				                
    				        //archivo = nombre con extension tmp
					        archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					        archivos = setStringFiles(archivos, archivo);
					        //archivo = la direccion completa donde se encientra el archivo
					        archivo = stringFileWithExtTmp(smbFile.getCanonicalPath());

					        noEmpresa = 0;
					        
					        //Leer el archivo actual.
					        
					        try {

					        	BufferedReader reader = null;
					        	reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(archivo)));
					  		  	String lineReader = null;
					  		    archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					  		  	
					  		  try{
					  			   
					  			  //Lee una linea
					  			  while ((lineReader = reader.readLine()) != null) {
					  				  I += 1;
					  				  registro = lineReader;
					  				  registro = registro.replace("'", " ");
					  				  int op = Integer.parseInt(registro.substring(0, 2));
					  				  
					  				  switch (op) {
										case 00: //Solo inicio de archivo, ningun valor nos interesa para el proceso
											break;
										case 11:
											/*'Registro de cabecera de cuenta
	                            			De la cabecera de la cuenta se toma el numero de cuenta
	                            			REGISTRO, CLAVE PAIS, SUCURSAL, CUENTA, FECHA INICIAL, FECHA FINAL, _
	                            			SALDO 2[+] / 1[-], SALDO INICIAL, MONEDA ALFABETICA, DIGITO CUENTA CLABE, _
	                            			TITULAR DE LA CUENTA, PLAZA CUENTA CLABE */
											
											chequeraNueva = registro.substring(10,20);
											
											List<EmpresaDto> empresas = new ArrayList<>();
					  			              
					  			              empresas = objBancaBuzonesDao.buscarEmpresa(chequeraNueva, BANCOMER, true);
					  			              
					  			              //consulta de chequera 
					  			              if(empresas.size() > 0){
					  			            	  noEmpresa = empresas.get(0).getNoEmpresa();
					  			            	  chequera = empresas.get(0).getIdChequera();
					  			              }else{
					  			            	  regSinChequeras += 1;
				  			            		  chequerasInexistentes = chequerasInexistentes + chequeraNueva + "\n";
				  			            		  //REVISAR SI APLICA
				  			            		  while ((lineReader = reader.readLine()) != null) {
				  			            			 registro = lineReader;
				  			            			 registro = registro.replace("'", " ");
				  			            			 if(registro.substring(0,2).equals("33")){
				  			            				 break;
				  			            			 }
				  			            		 }
				  			            		  //****************************//
				  			            		  //siguiente registro
				  			            		 continue;
					  			              }
					  			              fechaSaldo = registro.substring(30, 32) + "/" + registro.substring(28,30) + "/20" + registro.substring(26,28);
											break;
										case 22: /*'Registro principal de movimiento
				                            'REGISTRO, CLAVE PAIS, SUCURSAL, FECHA OPERACI�N, FECHA VALOR, CVE CONCILIACION AGREGADA, _
				                            CLAVE LEYENDA, CARGO (1) / ABONO (2), IMPORTE, DATO, CONCEPTO
				                            'Para Archivos SH  0 - Abono, 1 - Cargo, 2 � Saldo, 3 � Saldo Negativo 	*/
				                            if(registro.substring(27,28).equals("1")){
				                            	//tipoMovto = "1";
				                            	cargoAbono = "E";
				                            	sbCobro = "";
				                            }else if(registro.substring(27,28).equals("2")){
				                            	//tipoMovto = "0";
				                            	cargoAbono = "I";
				                            	if(registro.substring(52,80).equals("DEP S B COBRO")){
				                            		sbCobro = "N";
				                            	}else{
				                            		sbCobro = "S";
				                            	}
				                            }
				                            fecha = registro.substring(20, 22) + "/" + registro.substring(18,20) + "/20" + registro.substring(16,18);
				                            if(!funciones.isDate(fecha, false)){
				                            	//Se salta el complementario de movimiento
				                            	/*Original 
				                            	Line Input #1, ps_registro
				                                GoTo sigue_leyendo1 */
				                                if((lineReader = reader.readLine()) != null){
				                                	continue;
				                                }
				                                
				                            }
				                            
				                            concepto = registro.substring(52,80).trim(); //53,28
				                            importe = Double.parseDouble(registro.substring(28,40)+ "." + registro.substring(40,42)); //28,12
				                            sucursal = registro.substring(6,10); //7,4
				                            cveConcepto = registro.substring(24,27).trim(); //25,3
											break;
										case 23: //'Registro complementario de movimiento - 'REGISTRO, CODIGO DATO, REFERENCIA AMPLIADA, REFERENCIA
											contador+=1;
											regLeidos += 1;
											//  el folio varia de posicion en funcion del concepto (Se pueden basar en la lectura del MD)
											folioBanco = "";
											//se agrega la condicion de la longitud del concepto, xq si no truena si la cadena no alcanza los caracteres para comparar.
											if(concepto.length() >= 19 && concepto.substring(0, 19).equals("DEPOSITO DE TERCERO")){ //1,19
												folioBanco = registro.substring(42, 49); //43,7
											}else if(concepto.length() >= 22 && concepto.substring(0, 22).equals("TRASPASO ENTRE CUENTAS")){ //1,22
												folioBanco = registro.substring(42, 49); //43,7
											}else if(concepto.length() >= 12 && concepto.substring(0, 12).equals("SPEI ENVIADO")){ //1,22
												concepto = "SPEI ENVIADO";
												folioBanco = registro.substring(45, 52); //46,7
											}else if(concepto.length() >= 20 && concepto.substring(0, 20).equals("DEPOSITO EN EFECTIVO")){ //1,20
												folioBanco = registro.substring(42, 49); //43,7
											}else if(concepto.length() >= 19 && concepto.substring(0, 19).equals("TRASPASO A TERCEROS")){ //1,19
												folioBanco = registro.substring(42, 49); //43,7
											}else if(concepto.length() >= 13 && concepto.substring(0, 13).equals("SPEI RECIBIDO")){ //1,13
												concepto = "SPEI RECIBIDO";
												folioBanco = registro.substring(45, 52); //46,7
											}else if(concepto.length() >= 12 && concepto.substring(0, 12).equals("TEF RECIBIDO")){ //1,12
												concepto = "TEF RECIBIDO";
												folioBanco = registro.substring(45, 52); //46,7
											}
											
											descripcion = registro.substring(4, 42); //4,38
											
											if(concepto.indexOf("CHEQUE PAGADO NO") == 0){
												concepto = "CHEQUE PAGADO NO";
												referencia = registro.substring(42,72).trim(); //43,30
												//referencia = (funciones.isNumeric(referencia))? :referencia;
												//Original - psReferencia = IIf(IsNumeric(psReferencia), Val(psReferencia), psReferencia)
											}else{
												referencia = registro.substring(42,72).trim(); //43,30
											}
											
											//Estamos leyendo el layout del estado de cuenta
											Map<String, Object> map2 = new HashMap<>();										
											map2 = objBancaBuzonesDao.selectMovtoBancaE(folioBanco, "MISMO BANCO BANCOMER", "", noEmpresa, chequera, fecha, importe, 0d , 0l , cargoAbono, BANCOMER);
											if(map2.get("estatus").equals("")){
												List<MovtoBancaEDto> listMovimientos = new ArrayList<>();
												listMovimientos = (List<MovtoBancaEDto>) map2.get("listaMovto");
												if(listMovimientos.size() <= 0){
													folio = folioReal("secuencia");
													if(folio <= 0){
														return false;
													}
													leeNetCashC43 = true;	
													
													/*String aux = objBancaBuzonesDao.insertaMovtoBancaEStr(noEmpresa, BANCOMER, chequera, folio, 
															 fecha, sucursal, folioBanco ,referencia, cargoAbono, importe,
															 sbCobro, concepto, idUsuario, descripcion, archivo,
															 fechaHoy, observacion, cveConcepto, 0d, estatus,
															 contador, "", "", ""); //0d=saldo, ultimos cuatro parametros: noLineaArchivo, hora, fecha operacion y estatus_1
													
													inserts.append(aux); //agregar a una lista
													*/
													
													//Insertamos los movimientos en una lista para despues hacer un insert de todos los 
													MovtoBancaEDto movto = new MovtoBancaEDto();
													movto.setNoEmpresa(noEmpresa);
													movto.setIdBanco(BANCOMER);
													movto.setIdChequera(chequera);
													movto.setSecuencia(folio);
													movto.setFecValor(funciones.ponerFechaDate(fecha) ); //checar dato que retorna la conversion
													movto.setSucursal(sucursal);
													movto.setFolioBanco(folioBanco);
													movto.setReferencia(referencia);
													movto.setCargoAbono(cargoAbono);
													movto.setImporte(importe);
													movto.setBSalvoBuenCobro(sbCobro);
													movto.setConcepto(concepto);
													movto.setUsuarioAlta(idUsuario);
													movto.setDescripcion(descripcion);
													movto.setArchivo(archivo);
													movto.setFechaAlta(fechaHoy);
													movto.setObservacion(observacion);
													movto.setIdCveConcepto(cveConcepto);
													movto.setSaldoBancario(0d);
													movto.setEstatus(estatus);
													movto.setMovtoArch(contador);
													movto.setFechaOperacion(fecha);
													movto.setIdEstatusTrasp(estatus);
													lMovimientos.add(movto);
													
													regImportados += 1;
													encontrados += 1;
													
												}
												
											}
											break;										
										case 32:
											/*Case "32" 'Registro complementario de fin de cuenta
				                            'REGISTRO, CLAVE PAIS, SUBCODIGO DE REGISTRO, INFORMACION 1*, INFORMACION 2*
				                            */
											break;
										case 33:
											/*
											'Registro de fin de cuenta
				                            'REGISTRO, CLAVE PAIS, SUCURSAL, CUENTA, No. DE CARGOS, IMPORTE TOTAL DE CARGOS, _
				                             No. DE ABONOS, IMPORTE TOTAL DE ABONOS, SALDO 2[+] / 1[-], SALDO FINAL, MONEDA ALFABETICA _
				                             LIBRE
				                             */
											
											cargoAbono = "S";
											/*if(registro.substring(58,59).equals("1")){ //59,1
												//tipoMovto = "3";
											}else{
												//tipoMovto = "2";
											}*/
				                            
											sbCobro = "";
				                            fecha = fechaSaldo;
				                            
				                            if(!funciones.isDate(fecha, false)){
				                            	continue;
				                            }
				                            
				                            concepto = "SALDO ULTIMA TRANS" ;
				                            importe = Double.parseDouble(registro.substring(59,71) + "." + registro.substring(71,73)); //59,71 - 72,2 
				                            sucursal = registro.substring(6,10); //7,4
				                            cveConcepto = "";
				                            contador += 1; 
				                            regLeidos += 1;
				                            folioBanco = "0";
				                            descripcion = "";
				                            referencia = "";
				                            
				                            //Verificar que maneje actualizaci�n de saldos
				                            if(saldoBanco.equals("R")){
				                            	
				                            	if(lMovimientos.size()>0){
				                            		objBancaBuzonesDao.ejecutaInsertsMovtoBancaE(lMovimientos); //cambiar por lista
				                            		lMovimientos.clear();
				                            	}
				                            	
				                            	saldoChequera = Double.parseDouble(registro.substring(59, 71)+"."+registro.substring(71,73)); //60,12 - 72,2
				                            	if(registro.substring(58,59).equals("1")){
				                            		saldoChequera = saldoChequera * -1;
				                            	}
				                            	
				                            	actualizarSaldoChequeraBancomer(saldoChequera, archivo, 12, chequera);
				                            	
				                            	actualizarSaldoChequera(saldoChequera, noEmpresa, 12, chequera);
				                            	 
				                                 leeNetCashC43 = true;
				                                 
				                            }else{
				                            	reader.close();
				                            	return leeNetCashC43;
				                            }
				                            
				                            Map<String, Object> map3 = objBancaBuzonesDao.selectMovtoBancaE(folioBanco, "MISMO BANCO BANCOMER", "", noEmpresa, chequera, fecha, importe, 0d, 0l, cargoAbono, BANCOMER);
				                            if(String.valueOf(map3.get("estatus")).equals("")){
				                            	
				                            	List<MovtoBancaEDto> movimientos = new ArrayList<>();
				                            	movimientos = (List<MovtoBancaEDto>) map3.get("listaMovto");
				                            	//Revisa si ya existe el movimiento
				                            	if(movimientos.size() <= 0){
				                            		
				                            		folio = folioReal("secuencia");
				                            		if(folio <= 0){
				                            			return false;
				                            		}
				                            		
				                            		leeNetCashC43 = true;
				                            		
				                            		/*String aux = objBancaBuzonesDao.insertaMovtoBancaEStr(noEmpresa, BANCOMER, chequera, folio, 
															 fecha, sucursal, folioBanco ,referencia, cargoAbono, importe,
															 sbCobro, concepto, idUsuario, descripcion, archivo,
															 fechaHoy, observacion, cveConcepto, 0d, estatus,
															 contador, "", "", ""); //0d=saldo, ultimos cuatro parametros: noLineaArchivo, hora, fecha operacion y estatus_1
				
													
													inserts.append(aux);*/
				                            		
				                            		//Insertamos los movimientos en una lista para despues hacer un insert de todos los 
													MovtoBancaEDto movto = new MovtoBancaEDto();
													movto.setNoEmpresa(noEmpresa);
													movto.setIdBanco(BANCOMER);
													movto.setIdChequera(chequera);
													movto.setSecuencia(folio);
													movto.setFecValor(funciones.ponerFechaDate(fecha) ); //checar dato que retorna la conversion
													movto.setSucursal(sucursal);
													movto.setFolioBanco(folioBanco);
													movto.setReferencia(referencia);
													movto.setCargoAbono(cargoAbono);
													movto.setImporte(importe);
													movto.setBSalvoBuenCobro(sbCobro);
													movto.setConcepto(concepto);
													movto.setUsuarioAlta(idUsuario);
													movto.setDescripcion(descripcion);
													movto.setArchivo(archivo);
													movto.setFechaAlta(fechaHoy);
													movto.setObservacion(observacion);
													movto.setIdCveConcepto(cveConcepto);
													movto.setSaldoBancario(0d);
													movto.setEstatus(estatus);
													movto.setMovtoArch(contador);
													movto.setFechaOperacion("");
													movto.setIdEstatusTrasp("S");
													
													lMovimientos.add(movto);
													
													regImportados += 1;
													encontrados += 1;
													
				                            	}
				                            	
				                            }
											break;
											
										case 88:
											/*
											 * 'SOLO FINAL DE ARCHIVO
				                            'La ultima linea es pie y ningun valor nos interesa para el proceso
				                            'REGISTRO, CAMPO FIJO No. 9 (18), No. DE REGISTROS (contando cabecera pero no el pie), LIBRE
				                            'Se puede hacer las validaciones para checar que el archivo no este alterado
											 */
											break;
										default:
											break;
										}
			  			              
					  			  }//fin while lector de lineas archivos
					  			  
					  			if(lMovimientos.size()>0){
                            		objBancaBuzonesDao.ejecutaInsertsMovtoBancaE(lMovimientos); //cambiar por lista
                            		lMovimientos.clear();
                            	}
                            						  			  
					  			  if(archivOk || encontrados == I){
					  				  renombrarArchivo(new SmbFile(stringFileWithExtTmp(smbFile.getCanonicalPath())), "old", true);
					  				  objBancaBuzonesDao.insertaArchMovtoBancaE(smbFile.getName(), chequera.substring(chequera.length()-8 , chequera.length()), fechaHoy, BANCOMER);
					  			  }
					  		        
					  		   }catch (IOException exception) {
					  			   bitacora.insertarRegistro(new Date().toString() + " " + exception.toString()+ " "
				    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43");
					  		   }finally {
					  			   try {
					  				   reader.close();
					  			   } catch (IOException ex) {
					  				 bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()+ " "
					    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43");
					  			   }
					  		   }
					  		  
			        			
			        		}catch(Exception e){
			        			 bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ " "
		    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43");
			        		}
					        
    					}//Fin if de renombrado a tmp
    					
    					//Falta insertar en bitacora
    					//Call FunSQLInsertBitacora(BANCOMER, ps_Archivo, Format(Date, "dd/mm/yyyy"), Format(Time, "hh:mm:ss"))
    					
    				}//fin foreach - siguiente archivo.
    				
    				this.archivos = archivos;
    				this.chequerasInexistentes = chequerasInexistentes;
    				
    				return leeNetCashC43;
    			}else{
    				 bitacora.insertarRegistro(new Date().toString() + " " + "No hay archivos a procesar en el directorio."
						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43");
    				return false;
    			}
    			
    		}else{
    			 bitacora.insertarRegistro(new Date().toString() + " " + String.valueOf(mapa.get("msgUsuario"))
    						+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43");
    			return false;
    		}
    		
		}else {
			 bitacora.insertarRegistro(new Date().toString() + " " + "msgUsuario: No se encontraron las variantes para generar la ruta"
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43");
		}
		
		return false;
		
	}

	private boolean leeNetCashC43Diario(String archivos, String chequerasInexistentes, long regLeidos,
			  long regSinChequeras, long regImportados){

		boolean archivOk;
		String archivo;
		String fechaValor;
		int noEmpresa;
		String registro;
		String chequera;
		long folio;
		String sucursal;
		String folioBanco;
		String referencia;
		String cargoAbono;
		double importe;
		String concepto;
		String descripcion;
		String cveConcepto;
		String chequeraNueva; 
		int encontrados;
		double saldoChequera;
		int I;
		List<MovtoBancaEDto> lMovimientos = new ArrayList<>();
		/**************/
		cveConcepto = "";
		archivo = "";
		registro = "";
		regLeidos = 0;
		concepto = "";
		fechaValor = "";
		importe = 0;
		cargoAbono = "";
		sucursal = "";
		encontrados = 0;
		I = 0;

		List<String> valoresConexion =  objBancaBuzonesDao.armaCadenaConexion();

		String dirPrincipal = "";
		System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
		if (valoresConexion.size()!=0 ) {
			dirPrincipal = "smb://"+valoresConexion.get(0)+":"+valoresConexion.get(1)+
					"@"+valoresConexion.get(2)+"/"+valoresConexion.get(3);

			System.out.println("dirPrincipal: "+dirPrincipal);

			Map<String, Object> mapa = crearDirectorioLayout(dirPrincipal, "/bancomer/netcashC43/diario/");

			if((boolean) mapa.get("resExito")){
				//Obtiene todos los archivos que contenga el directorio y que se van a leer .txt.
				SmbFile archivosSmb[] = obtenerArchivosDirectorio(dirPrincipal+"/bancomer/netcashC43/diario/");  			

				//Map<String, Object> map = new HashMap<>();

				//Se comenta xq no se usa para nada, aunq esta en el modelo.
				/*map = objBancaBuzonesDao.verificaSaldoBanco(12); //Buscar saldo banco

				if(!String.valueOf(mapa.get("saldo")).equals("")){
					//saldoBanco = String.valueOf(mapa.get("saldo"));
				}
				 */
				
				if(archivosSmb.length > 0){

					for(SmbFile smbFile: archivosSmb){
						I=0;
						if(renombrarArchivo(smbFile, "tmp", false)){
		
							chequera = "";
							//contador = 0;
							archivOk = true;
							
							//archivo = nombre con extension tmp
					        archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					        archivos = setStringFiles(archivos, archivo);
					        //archivo = la direccion completa donde se encientra el archivo
					        archivo = stringFileWithExtTmp(smbFile.getCanonicalPath());

					        noEmpresa = 0;
      
							//Leer el archivo actual.
      
							try {

								BufferedReader reader = null;
								reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(archivo)));
								String lineReader = null;
								archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
								
								try{
			   
									//Lee una linea
									while ((lineReader = reader.readLine()) != null) {
										I += 1;
										registro = lineReader;
										registro = registro.replace("'", " ");
										int op = Integer.parseInt(registro.substring(0, 2));
				  
										switch (op) {
										case 00: //Solo inicio de archivo, ningun valor nos interesa para el proceso
											break;
										case 11:
											/*'Registro de cabecera de cuenta
          									De la cabecera de la cuenta se toma el numero de cuenta */
											
											chequeraNueva = registro.substring(2,20); //3,18
						
											List<EmpresaDto> empresas = new ArrayList<>();
			              
											empresas = objBancaBuzonesDao.buscarEmpresaClabe(chequeraNueva, BANCOMER);
											
											if(empresas.size() > 0){
												noEmpresa = empresas.get(0).getNoEmpresa();
												chequera = empresas.get(0).getIdChequera();
											}else{
												regSinChequeras += 1;
												chequerasInexistentes = chequerasInexistentes + chequeraNueva + "\n";
												//REVISAR SI APLICA
												while ((lineReader = reader.readLine()) != null) {
													registro = lineReader;
													registro = registro.replace("'", " ");
													if(registro.substring(0,2).equals("11")){
														break;
													}
												}
												//****************************//
												//siguiente registro
												continue;
											}
											//fechaSaldo = registro.substring(30, 32) + "/" + registro.substring(28,30) + "/20" + registro.substring(26,28);
											break;											
										case 22: /*'Registro principal de movimiento
                      							'REGISTRO, CLAVE PAIS, SUCURSAL, FECHA OPERACI�N, FECHA VALOR, CVE CONCILIACION AGREGADA, _
                      							CLAVE LEYENDA, CARGO (1) / ABONO (2), IMPORTE, DATO, CONCEPTO
                      							'Para Archivos SH  0 - Abono, 1 - Cargo, 2 � Saldo, 3 � Saldo Negativo 	*/
												
											folioBanco = registro.substring(2,9); //3,7
											referencia = registro.substring(11,26); //12,15
											concepto = registro.substring(26, 51); //27,25
											descripcion = registro.substring(51,88); //52,37
											
											if(registro.substring(88,89).equals("1")){ //89,1
													//tipoMovto = "1";
													cargoAbono = "E";
											}else if(registro.substring(88,89).equals("0")){ //89,1
												//tipoMovto = "0";
												cargoAbono = "I";
											}
						                       
											importe = Double.parseDouble(registro.substring(89, 105)); //90,16
											saldoChequera = Double.parseDouble(registro.substring(105, 121)); //106,16
											cveConcepto = registro.substring(121, 124); //122,3
											sucursal = registro.substring(124,128); //125,4
						                    fechaValor = registro.substring(144,146) +"/"+ registro.substring(147,149) + "/20" + registro.substring(150,152); //145,2 - 148,2 - 151,2
						                    folio = folioReal("secuencia"); // psSecuencia
						                    
						                    boolean existe = objBancaBuzonesDao.existeMovDia2(noEmpresa, 12, chequera, fechaValor, referencia, cargoAbono, importe, concepto);
						                    
						                    
						                    if(!existe){
						                    	//Cambiar a llenar una lista para hacer todos los inserts de una sola llamada.
						                    	/*objBancaBuzonesDao.insertaMovtoBancaD(noEmpresa, 12, chequera, folio, fechaValor, sucursal,
						                    										  folioBanco, referencia, cargoAbono, importe, concepto, idUsuario,
						                    										  descripcion, archivo, I, saldoChequera, cveConcepto, fechaHoy);
						                    	*/
						                    	
						                    	//Insertamos los movimientos en una lista para despues hacer un insert de todos los 
												MovtoBancaEDto movto = new MovtoBancaEDto();
												movto.setNoEmpresa(noEmpresa);
												movto.setIdBanco(BANCOMER);
												movto.setIdChequera(chequera);
												movto.setSecuencia(folio);
												movto.setFecValor(funciones.ponerFechaDate(fechaValor));
												movto.setSucursal(sucursal);
												movto.setFolioBanco(folioBanco);
												movto.setReferencia(referencia);
												movto.setCargoAbono(cargoAbono);
												movto.setImporte(importe);
												movto.setBSalvoBuenCobro("");
												movto.setConcepto(concepto);
												movto.setUsuarioAlta(idUsuario);
												movto.setDescripcion(descripcion);
												movto.setArchivo(archivo);
												movto.setFechaAlta(fechaHoy);
												movto.setObservacion("");
												movto.setIdCveConcepto(cveConcepto);
												movto.setSaldoBancario(saldoChequera);
												movto.setEstatus("");
												movto.setMovtoArch(0);
												movto.setFechaOperacion("");
												movto.setNoLineaArch(I);
												
												lMovimientos .add(movto);
												
						                    	regImportados += 1;
						                    	encontrados += 1;
						                    	regLeidos += 1;
						                    }
										break;
										case 88:
											/*
											 * 'SOLO FINAL DE ARCHIVO
                      						'La ultima linea es pie y ningun valor nos interesa para el proceso
                      						'REGISTRO, CAMPO FIJO No. 9 (18), No. DE REGISTROS (contando cabecera pero no el pie), LIBRE
                      						'Se puede hacer las validaciones para checar que el archivo no este alterado
											 */
											break;
										default:
											break;
										}
										
									}//fin while lector de lineas archivos
										
									if(lMovimientos.size()>0){
	                            		objBancaBuzonesDao.ejecutaInsertsMovtoBancaD(lMovimientos); //cambiar por lista
	                            		objBancaBuzonesDao.insertaArchMovtoBancaE(smbFile.getName(), chequera.substring(chequera.length()-8 , chequera.length()), fechaHoy, BANCOMER);
	                            		lMovimientos.clear();
	                            	}
									
									if(archivOk || encontrados == I){
										renombrarArchivo(new SmbFile(stringFileWithExtTmp(smbFile.getCanonicalPath())), "old", true);
									}
									
								}catch (IOException exception) {
									bitacora.insertarRegistro(new Date().toString() + " " + exception.toString()+ " "
											+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43Diario");
								}finally {
									try {
										reader.close();
									} catch (IOException ex) {
										bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()+ " "
												+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43Diario");
									}
								}
								
							}catch(Exception e){
								bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ " "
										+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43Diario");
							}
							
						}//Fin if de renombrado a tmp
						
						archivo = "";
						
						//Falta insertar en bitacora
						//Call FunSQLInsertBitacora(BANCOMER, ps_Archivo, Format(Date, "dd/mm/yyyy"), Format(Time, "hh:mm:ss"))
						
					}//fin foreach - siguiente archivo.
					
					this.archivos = archivos;
					this.chequerasInexistentes = chequerasInexistentes;
					
					return true;
				}else{
					bitacora.insertarRegistro(new Date().toString() + " " + "No hay archivos a procesar en el directorio."
							+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43Diario");
					return false;
				}
				
			}else{
				bitacora.insertarRegistro(new Date().toString() + " " + String.valueOf(mapa.get("msgUsuario"))
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43Diario");
				return false;
			}
			
		}else {
			bitacora.insertarRegistro(new Date().toString() + " " + "msgUsuario: No se encontraron las variantes para generar la ruta"
					+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeNetCashC43Diario");
		}
		
		return false;
		
	}
	
	private void actualizarSaldoChequera(double saldoChequera, int noEmpresa, int idBanco, String chequera) {
		
		try {
			 objBancaBuzonesDao.updateSaldosCatCtaBanco(saldoChequera, noEmpresa, idBanco, chequera, fechaHoy);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + e.toString()
			+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:actualizarSaldoChequera");
		}
		
	}

	private void actualizarSaldoChequeraBancomer(double saldoChequeraOrig, String archivo, int idBanco , String chequera) {
		
		double saldoChequera = 0;
		long secuencia = 0;
		
		//Actualizar saldo de banco en chequeras con el maximo saldo del registro cuya fecha sea la mayor
	    //agrupando por banco, chequera y empresa
		try {
			Map<String, Object> map2 = objBancaBuzonesDao.selectMovtosBancomer(archivo, idBanco, chequera);
			//Si no hubo error
			if(String.valueOf(map2.get("estatus")).equals("")){
				List<MovtoBancaEDto> movimientos = new ArrayList<>();
				
				movimientos = (List<MovtoBancaEDto>) map2.get("lista");
				saldoChequera = saldoChequeraOrig;
				for(MovtoBancaEDto movimiento: movimientos){
					secuencia = movimiento.getSecuencia();
					//Actualizar el saldo de chequera por cada movimiento encontrado.
					objBancaBuzonesDao.updateSaldoBancomer(saldoChequera, archivo, idBanco, chequera, secuencia); 
					
					if(movimiento.getCargoAbono().equals("E")){
						saldoChequera += movimiento.getImporte();
					}else{
						saldoChequera -= movimiento.getImporte();
					}
			            
				}

			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + e.toString()
					+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:actualizarSaldoChequeraBancomer");
		}
		
	}
	
	public boolean leeBancoAzteca(){
		
		boolean archivOk;
		String archivo;
		String fechaValor;
		int noEmpresa;
		String registro;
		String chequera;
		long folio;
		String sucursal;
		String folioBanco;
		String referencia;
		String cargoAbono;
		double importe;
		String concepto;
		String descripcion;
		String cveConcepto;
		String chequeraNueva; 
		int encontrados;
		String archivos;
		String chequerasInexistentes;
		String fechaOperacion;
		
		int I;
		List<MovtoBancaEDto> lMovimientos = new ArrayList<>();
		/**************/
		archivos = "";
		chequerasInexistentes = "";
		cveConcepto = "";
		archivo = "";
		registro = "";
		concepto = "";
		fechaValor = "";
		importe = 0;
		cargoAbono = "";
		sucursal = "";
		encontrados = 0;
		I = 0;
		
		List<String> valoresConexion =  objBancaBuzonesDao.armaCadenaConexion();

		String dirPrincipal = "";
		System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
		if (valoresConexion.size()!=0 ) {
			dirPrincipal = "smb://"+valoresConexion.get(0)+":"+valoresConexion.get(1)+
					"@"+valoresConexion.get(2)+"/"+valoresConexion.get(3);

			System.out.println("dirPrincipal: "+dirPrincipal);

			Map<String, Object> mapa = crearDirectorioLayout(dirPrincipal, "/azteca/edo_cuenta/");

			if((boolean) mapa.get("resExito")){
				//Obtiene todos los archivos que contenga el directorio y que se van a leer .txt.
				SmbFile archivosSmb[] = obtenerArchivosDirectorio(dirPrincipal+"/azteca/edo_cuenta/");  			
				
				if(archivosSmb.length > 0){

					for(SmbFile smbFile: archivosSmb){
						I=0;
						if(renombrarArchivo(smbFile, "tmp", false)){
		
							chequera = "";
							archivOk = true;
							
					        archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					        archivos = setStringFiles(archivos, archivo);
					        //archivo = la direccion completa donde se encientra el archivo
					        archivo = stringFileWithExtTmp(smbFile.getCanonicalPath());

					        noEmpresa = 0;
							//Leer el archivo actual.
      
							try {

								BufferedReader reader = null;
								reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(archivo)));
								String lineReader = null;
								archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
								
								try{
			   
									//Lee una linea
									while ((lineReader = reader.readLine()) != null) {
										I += 1;
										registro = lineReader;
										registro = registro.replace("'", " ");										

										chequeraNueva = registro.substring(0,18); //1,18
										
										List<EmpresaDto> empresas = new ArrayList<>();
		              
										empresas = objBancaBuzonesDao.buscarEmpresaClabe(chequeraNueva, AZTECA); //chequeraNuevo = clabe
										
										if(empresas.size() > 0){
											noEmpresa = empresas.get(0).getNoEmpresa();
											chequera = empresas.get(0).getIdChequera();
										}else{
											//regSinChequeras += 1;
											chequerasInexistentes = chequerasInexistentes + chequeraNueva + "\n";
											//siguiente registro
											continue;
										}
										
										fechaOperacion = registro.substring(26,28) + "/" + registro.substring(23, 25) + "/" + registro.substring(18, 22);
										fechaValor = registro.substring(36,38) +"/"+ registro.substring(33,35) + "/" + registro.substring(28,32); //37,2-34,2-
										sucursal = registro.substring(38,42); //39,4
										folioBanco = registro.substring(42,51); //43,9
										referencia = registro.substring(90,125); //91,35
										cargoAbono = (registro.substring(72,73).equals("+"))?"I":"E"; //73,1
										importe = Double.parseDouble(registro.substring(73, 90)); //74,17
										concepto = (registro.length() >= 158)?registro.substring(143, 158):""; //144,15
										descripcion = (registro.length() >= 243)?registro.substring(158,243):""; //159,85
										cveConcepto = registro.substring(69, 72); //70,3

					                    folio = folioReal("secuencia"); // psSecuencia
					                    
					                    boolean existe = objBancaBuzonesDao.existeMovBancaE(noEmpresa, AZTECA, chequera, fechaValor, referencia, cargoAbono, importe, concepto);
					                    
					                    if(!existe){
					                    	
					                    	//Insertamos los movimientos en una lista para despues hacer un insert de todos los 
											MovtoBancaEDto movto = new MovtoBancaEDto();
											movto.setNoEmpresa(noEmpresa);
											movto.setIdBanco(AZTECA);
											movto.setIdChequera(chequera);
											movto.setSecuencia(folio);
											movto.setFecValor(funciones.ponerFechaDate(fechaValor));
											movto.setSucursal(sucursal);
											movto.setFolioBanco(folioBanco);
											movto.setReferencia(referencia);
											movto.setCargoAbono(cargoAbono);
											movto.setImporte(importe);
											movto.setBSalvoBuenCobro("");
											movto.setConcepto(concepto);
											movto.setUsuarioAlta(idUsuario);
											movto.setDescripcion(descripcion);
											movto.setArchivo(archivo);
											movto.setFechaAlta(fechaHoy);
											movto.setObservacion("");
											movto.setIdCveConcepto(cveConcepto);
											//movto.setSaldoBancario(saldoChequera);
											movto.setEstatus("");
											movto.setMovtoArch(0);
											movto.setFechaOperacion("");
											movto.setNoLineaArch(I);
											movto.setIdEstatusTrasp("S");
											movto.setFechaOperacion(fechaOperacion);
											
											lMovimientos.add(movto);
											
					                    	//regImportados += 1;
					                    	encontrados += 1;
					                    	//regLeidos += 1;
					                    }
										
									}//fin while lector de lineas archivos
										
									if(lMovimientos.size()>0){
	                            		objBancaBuzonesDao.ejecutaInsertsMovtoBancaE(lMovimientos);
	                            		objBancaBuzonesDao.insertaArchMovtoBancaE(smbFile.getName(), chequera.substring(chequera.length()-8 , chequera.length()), fechaHoy, AZTECA);
	                            		lMovimientos.clear();
	                            	}
									
									if(archivOk || encontrados == I){
										renombrarArchivo(new SmbFile(stringFileWithExtTmp(smbFile.getCanonicalPath())), "old", true);
									}
									
								}catch (IOException exception) {
									bitacora.insertarRegistro(new Date().toString() + " " + exception.toString()+ " "
											+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
								}finally {
									try {
										reader.close();
									} catch (IOException ex) {
										bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()+ " "
												+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
									}
								}
								
							}catch(Exception e){
								bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ " "
										+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
							}
							
						}//Fin if de renombrado a tmp
						
						archivo = "";
						
						//Falta insertar en bitacora
						//Call FunSQLInsertBitacora(BANCOMER, ps_Archivo, Format(Date, "dd/mm/yyyy"), Format(Time, "hh:mm:ss"))
						
					}//fin foreach - siguiente archivo.
					
					this.archivos = archivos;
					this.chequerasInexistentes = chequerasInexistentes;
					
					return true;
				}else{
					bitacora.insertarRegistro(new Date().toString() + " " + "No hay archivos a procesar en el directorio."
							+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
					return false;
				}
				
			}else{
				bitacora.insertarRegistro(new Date().toString() + " " + String.valueOf(mapa.get("msgUsuario"))
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
				return false;
			}
			
		}else {
			bitacora.insertarRegistro(new Date().toString() + " " + "msgUsuario: No se encontraron las variantes para generar la ruta"
					+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
		}
		
		return false;
		
	}
	
	
	
	public boolean leeBancoAztecaMd(){
		
		boolean archivOk;
		String archivo;
		
		int noEmpresa;
		String registro;
		String chequera;
		long folio;
		String sucursal;
		
		
		
		
		String chequeraNueva; 
		int encontrados;
		String archivos;
		String chequerasInexistentes;
		
		String entidad;
		String centro;
		String sucursalOp;
		String folioBanco;
		String referencia;
		String cargoAbono;
		double importe;
		String saldoBuenCobro;
		String concepto;
		String fechaOperacion;
		String fechaValor;
		String idClaveConcepto;
		
		String descripcion;
		
		
		int I;
		List<MovtoBancaEDto> lMovimientos = new ArrayList<>();
		/**************/
		archivos = "";
		chequerasInexistentes = "";
		
		archivo = "";
		registro = "";
		concepto = "";
		fechaValor = "";
		importe = 0;
		cargoAbono = "";
		sucursal = "";
		encontrados = 0;
		I = 0;
		
		List<String> valoresConexion = objBancaBuzonesDao.armaCadenaConexion();

		String dirPrincipal = "";
		System.setProperty("jcifs.smb.client.domain", objBancaBuzonesDao.consultarConfiguraSet(3116));
		if (valoresConexion.size()!=0 ) {
			dirPrincipal = "smb://"+valoresConexion.get(0)+":"+valoresConexion.get(1)+
					"@"+valoresConexion.get(2)+"/"+valoresConexion.get(3);

			System.out.println("dirPrincipal: "+dirPrincipal);

			Map<String, Object> mapa = crearDirectorioLayout(dirPrincipal, "/azteca/mismo_dia/");

			if((boolean) mapa.get("resExito")){
				//Obtiene todos los archivos que contenga el directorio y que se van a leer .txt.
				SmbFile archivosSmb[] = obtenerArchivosDirectorio(dirPrincipal+"/azteca/mismo_dia/");  			
				
				if(archivosSmb.length > 0){

					for(SmbFile smbFile: archivosSmb){
						I=0;
						if(renombrarArchivo(smbFile, "tmp", false)){
		
							chequera = "";
							archivOk = true;
							
					        archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
					        archivos = setStringFiles(archivos, archivo);
					        //archivo = la direccion completa donde se encientra el archivo
					        archivo = stringFileWithExtTmp(smbFile.getCanonicalPath());

					        noEmpresa = 0;
							//Leer el archivo actual.
      
							try {

								BufferedReader reader = null;
								reader = new BufferedReader(new InputStreamReader(new SmbFileInputStream(archivo)));
								String lineReader = null;
								archivo =  smbFile.getName().substring(0, smbFile.getName().length() -3 ) + "tmp";
								
								try{
			   
									//Lee una linea
									while ((lineReader = reader.readLine()) != null) {
										I += 1;
										registro = lineReader;
										registro = registro.replace("'", " ");										

										chequeraNueva = registro.substring(8,18); //1,18
										
										List<EmpresaDto> empresas = new ArrayList<>();
		              
										empresas = objBancaBuzonesDao.buscarEmpresa(chequeraNueva, 127, true); //chequeraNuevo = clabe
										
										if(empresas.size() > 0){
											noEmpresa = empresas.get(0).getNoEmpresa();
											chequera = empresas.get(0).getIdChequera();
										}else{
											//regSinChequeras += 1;
											chequerasInexistentes = chequerasInexistentes + chequeraNueva + "\n";
											//siguiente registro
											continue;
										}
										
										
										entidad = registro.substring(0, 4);
										centro = registro.substring(4, 8);
										sucursalOp = registro.substring(18, 22);
										folioBanco = registro.substring(22, 31).trim();
										referencia = registro.substring(31, 46);
										cargoAbono = registro.substring(46, 47);
										importe = Double.parseDouble(registro.substring(47, 65).trim());
										saldoBuenCobro = registro.substring(65, 66).trim(); ;
										concepto = registro.substring(66, 101).trim(); 
										fechaOperacion = registro.substring(109,111) + "/" + registro.substring(106, 108) + "/" + registro.substring(101, 105);
										fechaValor = registro.substring(119, 121) + "/" + registro.substring(116, 118) + "/" + registro.substring(111, 115);
										idClaveConcepto = registro.substring(121, 124);
										descripcion = registro.substring(124, registro.length());
										
					                    folio = folioReal("secuencia"); // psSecuencia
					                    
					                    boolean existe = objBancaBuzonesDao.existeMovDia2(noEmpresa, AZTECA, chequera, fechaValor, referencia, cargoAbono, importe, concepto);
					                    
					                    if(!existe){
					                    	
					                    	//Insertamos los movimientos en una lista para despues hacer un insert de todos los 
											MovtoBancaEDto movto = new MovtoBancaEDto();
											movto.setNoEmpresa(noEmpresa);
											movto.setIdBanco(AZTECA);
											movto.setIdChequera(chequera);
											movto.setSecuencia(folio);
											movto.setFecValor(funciones.ponerFechaDate(fechaValor));
											movto.setSucursal(sucursalOp);
											movto.setFolioBanco(folioBanco);
											movto.setReferencia(referencia);
											movto.setCargoAbono(cargoAbono);
											movto.setImporte(importe);
											movto.setBSalvoBuenCobro(saldoBuenCobro);
											movto.setConcepto(concepto);
											movto.setUsuarioAlta(idUsuario);
											movto.setDescripcion(descripcion);
											movto.setArchivo(archivo);
											movto.setFechaAlta(fechaHoy);
											movto.setObservacion("");
											movto.setIdCveConcepto(idClaveConcepto);
											movto.setEstatus("");
											movto.setMovtoArch(0);
											movto.setFechaOperacion(fechaOperacion);
											movto.setNoLineaArch(I);
											//movto.setIdEstatusTrasp("S");
											
											lMovimientos.add(movto);
											
					                    	//regImportados += 1;
					                    	encontrados += 1;
					                    	//regLeidos += 1;
					                    }
										
									}//fin while lector de lineas archivos
										
									if(lMovimientos.size()>0){
										objBancaBuzonesDao.ejecutaInsertsMovtoBancaD(lMovimientos);
	                            		objBancaBuzonesDao.insertaArchMovtoBancaE(smbFile.getName(), chequera.substring(chequera.length()-8 , chequera.length()), fechaHoy, AZTECA);
	                            		lMovimientos.clear();
	                            	}
									
									if(archivOk || encontrados == I){
										renombrarArchivo(new SmbFile(stringFileWithExtTmp(smbFile.getCanonicalPath())), "old", true);
									}
									
								}catch (IOException exception) {
									bitacora.insertarRegistro(new Date().toString() + " " + exception.toString()+ " "
											+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
								}finally {
									try {
										reader.close();
									} catch (IOException ex) {
										bitacora.insertarRegistro(new Date().toString() + " " + ex.toString()+ " "
												+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
									}
								}
								
							}catch(Exception e){
								bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)+ " "
										+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
							}
							
						}//Fin if de renombrado a tmp
						
						archivo = "";
						
						//Falta insertar en bitacora
						//Call FunSQLInsertBitacora(BANCOMER, ps_Archivo, Format(Date, "dd/mm/yyyy"), Format(Time, "hh:mm:ss"))
						
					}//fin foreach - siguiente archivo.
					
					this.archivos = archivos;
					this.chequerasInexistentes = chequerasInexistentes;
					
					return true;
				}else{
					bitacora.insertarRegistro(new Date().toString() + " " + "No hay archivos a procesar en el directorio."
							+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
					return false;
				}
				
			}else{
				bitacora.insertarRegistro(new Date().toString() + " " + String.valueOf(mapa.get("msgUsuario"))
				+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
				return false;
			}
			
		}else {
			bitacora.insertarRegistro(new Date().toString() + " " + "msgUsuario: No se encontraron las variantes para generar la ruta"
					+"P:BancaElectronica, C:BancaBuzonesBusinessImpl, M:leeBancoAzteca");
		}
		
		return false;
		
	}

	
}
