package com.webset.set.layout.business;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.FileSystemResource;

import com.webset.set.coinversion.dao.CoinversionDao;
import com.webset.set.coinversion.dto.BarridoChequerasDto;
import com.webset.set.coinversion.dto.SaldoChequeraDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.BusquedaArchivos;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;

/**
 * Clase de la pantalla de barrido automatico por saldos
 * que lee los layouts de los bancos respectivos para el barrido 
 * de sus chequeras.
 * @author Jessica Arelly Cruz Cruz
 * @since 10/08/2011
 *
 */
public class ImportarSaldosChequerasBusiness {
	private static Logger logger = Logger.getLogger(ImportarSaldosChequerasBusiness.class);
	private CoinversionDao coinversionDao;
	private Bitacora bitacora;
	private GlobalSingleton globalSingleton;
	private Funciones funciones;
	private int iImportados;
	private int arch;
	private int iNoEmpresa;
	private int iSecuencia;
	private double dSaldoChequera;
	private boolean bTransaccion;
	private boolean bArchivoOk;
	private boolean bFecha;
	private String sArchivo;
	private String sExisteArchivo;
	private String sRegistro;
	private String sFecha;
	private String sHora;
		private String sFechaHora;
	private String sChequera;
	private String sDato;
	private String rutaActual;
	private File[] nombresArchivo;
	private Scanner archivoLeer;
	BusquedaArchivos busquedaArchivos;
	
	public void inicializar(){
		iImportados = 0;
		arch = 0;
		iNoEmpresa = 0;
		iSecuencia = 0;
		dSaldoChequera = 0;
		bTransaccion = false;
		bArchivoOk = false;
		bFecha = false;
		sArchivo = "";
		sExisteArchivo = "";
		sRegistro = "";
		sFecha = "";
		sFechaHora = "";
		sChequera = "";
		sDato = "";
		rutaActual = System.getProperty("user.dir");
		archivoLeer = null;
		busquedaArchivos = new BusquedaArchivos();
		funciones = new Funciones();
		bitacora = new Bitacora();
	}
	
	

	/**
	 * Function importar_saldos_BANAMEX_2004
	 * @param sRutaRegreso
	 * @return 
	 */
	public List<String> importarSaldosBanamex(String sRutaRegreso){
		String psBanamex = "";
		String sCampo = "";
		List<String> mensajes = new ArrayList<String>();
		Calendar calendar = Calendar.getInstance();
		this.inicializar();
		
		try{
			globalSingleton = GlobalSingleton.getInstancia();
		    bTransaccion = false;
		    psBanamex = sRutaRegreso + "\\saldos_Banamex_2004";
		    
		    if (!psBanamex.equals(rutaActual))
				System.setProperty("user.dir", psBanamex); //nos cambiamos al directorio de archivos de movimientos
		   
		    //toma el primer archivo .txt del directorio en el que est� posicionado
		    iImportados = 0;
		    nombresArchivo = busquedaArchivos.obtenerNombreArchivos(".txt");
		    
		    siguienteArchivo:
		    while (arch < nombresArchivo.length){
		    	bArchivoOk = true;
	            bFecha = false;
	            archivoLeer = new Scanner(nombresArchivo[arch]);
	            sArchivo = nombresArchivo[arch].getName();
	            sExisteArchivo = this.existeArchivo(sArchivo, "BANAMEX");
	            if(!sExisteArchivo.equals(""))
	            {
	            	mensajes.add(""+sExisteArchivo);
	            	sArchivo = nombresArchivo[arch].getName().trim().replace(".txt", ".old");
	            	this.renombrarArchivo(nombresArchivo[arch], ".txt", ".old");
	            	arch ++;
	            	continue siguienteArchivo;
	            }
	            
	            if(archivoLeer == null)
        			break;
	            
	            while (archivoLeer.hasNext()){ //repite hasta el fin de archivo
	            	sRegistro = archivoLeer.nextLine();
	            	
	            	if(sRegistro == null)
	            		break;
	            	
	            	if(sRegistro.length() == 0)
	            		sRegistro = archivoLeer.nextLine();
	            	
	            	sRegistro = sRegistro.replace("'", " ");
	            	calendar.setTime(globalSingleton.getFechaHoy());
	            	sFecha = (sRegistro.substring(0, 6) + calendar.get(Calendar.YEAR)).trim();
	            	//sFecha = funciones.quitarCaracteresRaros(sFecha, true).trim();
	            	
	            	if(funciones.isDate(sFecha, false) == true && sFecha.length() == 10){
	            		sFecha = funciones.ponerFechaSola(sFecha);
	            		bFecha = true;
	            		sRegistro = archivoLeer.nextLine();
	            		
	            		if(sRegistro.length() == 0)
    	            		sRegistro = archivoLeer.nextLine();
	            		
	            		sRegistro = sRegistro.replace("'", " ");
	            		
	            		if(!archivoLeer.hasNextLine())
	            			break;
	            		
	            		sigueLeyendo:
	            			while(archivoLeer.hasNextLine())
	            			{
	            				sRegistro = archivoLeer.nextLine();
	            				
	            				if(sRegistro.length() == 0)
	            					do{
	            						sRegistro = archivoLeer.nextLine();
	            					}while(sRegistro.length() == 0);
	            				
	    	            		sRegistro = sRegistro.replace("'", " ");
	    	            		bTransaccion = true;
	    	            		//si es el registro de la chequera
	    	            		sCampo = sRegistro.substring(0, 7).trim();
	    	            		
	    	            		if(!sCampo.equals("") && funciones.isNumeric(sCampo))		//solo considerar hasta que inicie la numeracion
	    	            		{
	    	            			if(sRegistro.indexOf("/", 31) > 1){
	    	            				sChequera = funciones.ponerCeros(sRegistro.substring(31, sRegistro.indexOf("/", 31)).trim(), 4) + "" + 
	    	            							funciones.ponerCeros(sRegistro.substring((sRegistro.indexOf("/", 31) + 1), (sRegistro.indexOf("/", 31) + 8)).trim(), 7);
	    	            			}
	    	            			else{
	    	            				sChequera = sRegistro.substring(31, 38);
	    	            			}
	    	            			
	    	            			List<BarridoChequerasDto> listEmpresa = new ArrayList<BarridoChequerasDto>();
	    	            			listEmpresa = coinversionDao.consultarChequeraConcentradora(ConstantesSet.BANAMEX, sChequera);
	    	            			if(listEmpresa.size() > 0){
	    	            				iNoEmpresa = listEmpresa.get(0).getNoEmpresa(); 
	    	            			}
	    	            			else{
	    	            				continue sigueLeyendo;
	    	            			}
	    	            			listEmpresa.clear();
	    	            			sFechaHora = sFecha + " " + sRegistro.substring(8, 13); //fecha con hora
	    	            			dSaldoChequera = Double.parseDouble(sRegistro.substring(43, 65).replace(",", "").trim());
	    	            			
	    	            			//checa si ya existe el movimiento en Saldo_chequera
	    	            			List<SaldoChequeraDto> listSaldo = new ArrayList<SaldoChequeraDto>();
	    	            			listSaldo = coinversionDao.consultarSaldoChequera(ConstantesSet.BANAMEX, sChequera, sFechaHora);
	    	            			
	    	            			//si no existe, lo inserta
	    	            			if(listSaldo.size() <= 0){
	    	            				iSecuencia = this.obtenerFolioReal("secuencia_saldo");
	    	            				
	    	            				SaldoChequeraDto dto = new SaldoChequeraDto();
	    	            				dto.setNoEmpresa(iNoEmpresa);
	    	            				dto.setIdBanco(ConstantesSet.BANAMEX);
	    	            				dto.setIdChequera(sChequera);
	    	            				dto.setSecuencia(iSecuencia);
	    	            				dto.setSaldo(dSaldoChequera);
	    	            				dto.setArchivo(sArchivo);
	    	            				
	    	            				coinversionDao.insertaSaldoChequera(dto, sFechaHora);
	    	            				iImportados = iImportados + 1;
	    	            			}
	    	            			listSaldo.clear();
	    	            			
	    	            		}//if(funciones.isNumeric(sCampo))...
	    	            		bTransaccion = false;
	            			}//while(archivoLeer.hasNext())...
	            		
	            		archivoLeer.close();
	            		archivoLeer = null;
	            		if(bArchivoOk){
	            			sArchivo = nombresArchivo[arch].getName().trim().replace(".txt", ".old");
	            			this.renombrarArchivo(nombresArchivo[arch], ".txt", ".old");
	            		}
	            		break;
	            	} //if(funciones.isDate(sFecha, false) == true && sFecha.length() == 10)...
	            }//while (archivoLeer.hasNext())...
	            
	            if(bFecha == false){
	            	archivoLeer.close();
	            	mensajes.add("El archivo " + sArchivo + " No corresponde a saldos Banamex");
	            }
	            
	            if(iImportados > 0){
	            	this.guardarArchivoImportado(sArchivo, ConstantesSet.BANAMEX);
	            }
	            
	            arch ++;
		    }//loop de busqueda de archivos
		    mensajes.add("Chequeras Banamex importadas: " + iImportados);
		    
		}catch(Exception e){
			mensajes.add("Ocurrio un error en la lectura del archivo " + sArchivo);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:LeerLayout, C:ImportarSaldosChequerasBusiness, M:importarSaldosBanamex");
		}
		return mensajes;
	}
	/**
	 * Function importar_saldos_Santander
	 * @param sRutaRegreso
	 * @return 
	 */
	
	
	public List<String> importarSaldosSantander(String sRutaRegreso){
			String psSantander = "";
			String sCampo = "";
			List<String> mensajes = new ArrayList<String>();
			Calendar calendar = Calendar.getInstance();
			this.inicializar();
			
			try{
				globalSingleton = GlobalSingleton.getInstancia();
				bTransaccion = false;
				psSantander = sRutaRegreso + "\\saldos_Santander";
			    
				if (!psSantander.equals(rutaActual))
					System.setProperty("user.dir", psSantander);
			
			    //toma el primer archivo .txt del directorio en el que est� posicionado
				iImportados = 0;
			    nombresArchivo = busquedaArchivos.obtenerNombreArchivos(".txt");
			     
			     siguienteArchivo:
				    while (arch < nombresArchivo.length){
				    	bArchivoOk = true;
			            bFecha = false;
			            archivoLeer = new Scanner(nombresArchivo[arch]);
			            sArchivo = nombresArchivo[arch].getName();
			            sExisteArchivo = this.existeArchivo(sArchivo, "SANTANDER");
			            if(!sExisteArchivo.equals(""))
			            {
			            mensajes.add(""+sExisteArchivo);
			            	sArchivo = nombresArchivo[arch].getName().trim().replace(".txt", ".old");
			            	this.renombrarArchivo(nombresArchivo[arch], ".txt", ".old");
			            	arch ++;
			            	continue siguienteArchivo;
			            }
			            
			            if(archivoLeer == null)
		        			break;
		        		 
		        		 sigueLeyendo:
	            			while(archivoLeer.hasNextLine())
	            			{
	            				sRegistro = archivoLeer.nextLine();
	            				sRegistro = sRegistro.replace("'", " ");
	            				
	            				sFecha = sRegistro.substring(56, 58) + "/"+ sRegistro.substring(58, 60) +"/"+ sRegistro.substring(60, 64);
	            			
	            				// Se agrega para colocar los segundos a la hora
	            				sHora = sRegistro.substring(64, 69) + ":00" ;
	            				
	            				sFecha += " "+ sHora;
	            			
	            				if(sFecha.equals("NO DISPONIBLE"))
	            					continue sigueLeyendo;
			                
	            				if(sFecha.length() > 16)
	            					sFecha = sFecha.substring(0,19);
	            				
	            				
	            			
	            				if(!sFecha.equals("") && funciones.isDate(sFecha,true))
	            				{
	            					//sFecha = funciones.cambiarFecha(sFecha, false);
	            				
		            				if(sFecha.length() > 16)
		            					sFecha = sFecha.substring(0,16);
		            				bFecha = true;
		            				sChequera = sRegistro.substring(0, 11);
		            				if(sChequera.equals("NO DISPONIBLE"))
		            					continue sigueLeyendo;
		            				sChequera = funciones.eliminarCerosAlaIzquierda(sChequera.trim());
		            				
		            				if(sChequera.length() != 11)
		            				{
		            					sChequera = funciones.ponerCeros(sChequera, 11);
		            					sChequera = funciones.cadenaRight(sChequera, 9);
		            					sChequera = funciones.ponerCeros(sChequera, 11);
		            				}
		            		
		            				
					            	List<BarridoChequerasDto> list = new ArrayList<BarridoChequerasDto>();
		            				list = coinversionDao.consultarChequeraConcentradora(ConstantesSet.SANTANDER, sChequera);
		            				
		            				if(list.size() > 0)
		            					iNoEmpresa =list.get(0).getNoEmpresa();
		            				else
		            					continue sigueLeyendo;
		            				list.clear();
		            				list = null;
		            				
		            				sFechaHora = sFecha;
		            				
		            				sDato = sRegistro.substring(69,84);
		            				if(sDato.equals("NO DISPONIBLE"))
		            					continue sigueLeyendo;
		            				dSaldoChequera = Double.parseDouble(sDato)/100;			
	            			
	            			
	            					//checa si ya existe el movimiento en Saldo_chequera
		            				List<SaldoChequeraDto> listSaldo = new ArrayList<SaldoChequeraDto>();
		            				listSaldo = coinversionDao.consultarSaldoChequera(ConstantesSet.SANTANDER, sChequera, sFechaHora);
		            				if(listSaldo.size() <= 0){
	    	            				iSecuencia = this.obtenerFolioReal("secuencia_saldo");
	    	            				
	    	            				SaldoChequeraDto dto = new SaldoChequeraDto();
	    	            				dto.setNoEmpresa(iNoEmpresa);
	    	            				dto.setIdBanco(ConstantesSet.SANTANDER);
	    	            				dto.setIdChequera(sChequera);
	    	            				dto.setSecuencia(iSecuencia);
	    	            				dto.setSaldo(dSaldoChequera);
	    	            				dto.setArchivo(sArchivo);
	    	            				
	    	            				coinversionDao.insertaSaldoChequera(dto, sFechaHora);
	    	            				iImportados = iImportados + 1;
	    	            			}
		            				listSaldo.clear();
		            				listSaldo = null;
		            				bTransaccion = false;
		            				
	            				} //if(funciones.isDate(sFecha, true) && sFecha.length() == 19)...
	            			} //while(archivoLeer.hasNextLine())...
	            			
	            		archivoLeer.close();
			            archivoLeer = null;
			            if(bArchivoOk)
			            {
			            	sArchivo = nombresArchivo[arch].getName().trim().replace(".txt", ".old");
			            	this.renombrarArchivo(nombresArchivo[arch], ".txt", ".old");
			            }
			            
			            if(bFecha == false)
			            	mensajes.add("El archivo " + sArchivo + " No corresponde a saldos Santander");
			            	
			            if(iImportados > 0)
			            	this.guardarArchivoImportado(sArchivo, ConstantesSet.SANTANDER);
			            	
			            arch ++;
				    } //while (arch < nombresArchivo.length)...
			    
			    mensajes.add("Chequeras Santander importadas: " + iImportados);
			    
			}catch(Exception e){
				mensajes.add("Ocurrio un error en la lectura del archivo " + sArchivo);
				e.printStackTrace();
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
						+ "P:LeerLayout, C:ImportarSaldosChequerasBusiness, M:importarSaldosSantander");
			}
			return mensajes;
		}	
	            			
            			
	/**
	 * Function importar_saldos_BANCOMER
	 * @param sRutaRegreso
	 * @return
	 */
	public List<String> importarSaldosBancomer(String sRutaRegreso){
		String sBancomer = "";
		List<String> mensajes = new ArrayList<String>();
		this.inicializar();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			bTransaccion = false;
			sBancomer = "C:\\" + sRutaRegreso + "\\saldos_bancomer";
		    
			if (!sBancomer.equals(rutaActual))
				System.setProperty("user.dir", sBancomer);
		
		    //toma el primer archivo .txt del directorio en el que est� posicionado
			iImportados = 0;
		    nombresArchivo = busquedaArchivos.obtenerNombreArchivos(".csv");
		    
		    siguienteArchivo:
			    while (arch < nombresArchivo.length){
			    	bArchivoOk = true;
		            bFecha = false;
		            archivoLeer = new Scanner(nombresArchivo[arch]);
		            sArchivo = nombresArchivo[arch].getName();
		            sExisteArchivo = this.existeArchivo(sArchivo, "BANCOMER");
		            if(!sExisteArchivo.equals(""))
		            {
		            	mensajes.add(""+sExisteArchivo);
		            	sArchivo = nombresArchivo[arch].getName().trim().replace(".csv", ".old");
		            	this.renombrarArchivo(nombresArchivo[arch], ".csv", ".old");
		            	arch ++;
		            	continue siguienteArchivo;
		            }
		            
		            if(archivoLeer == null)
	        			break;
		            
		            sigueLeyendo:
            			while(archivoLeer.hasNextLine())
            			{
            				sRegistro = archivoLeer.nextLine();
            				sRegistro = sRegistro.replace("'", " ");
            				
            				sFecha = funciones.sacarDatoEntreComas(sRegistro, 11);
            				
            				// Se agrega para colocar los segundos a la hora 
            				if(sFecha.length() == 16)
            					sFecha= sFecha + ":00";
            				
            				if(sFecha.equals("NO DISPONIBLE") || sFecha.length() < 16)
            					continue sigueLeyendo;
		                
            				if(sFecha.length() > 16)
            					sFecha = sFecha.substring(0,19);
            				
            				 //cambiar la fecha si el a�o viene primero
            				if(!sFecha.equals("") && funciones.isNumeric(sFecha.substring(0, 4)))
            					if(Integer.parseInt(sFecha.substring(0, 4)) > 2000)
            						sFecha = sFecha.substring(8, 10) + "/" + sFecha.substring(5, 7) + "/" + sFecha.substring(0, 4) + sFecha.substring(10);
            				
            				if(!sFecha.equals("") && funciones.isDate(sFecha, true) && sFecha.length() == 19)
            				{
            					//sFecha = funciones.cambiarFecha(sFecha, false);
            				
	            				if(sFecha.length() > 16)
	            					sFecha = sFecha.substring(0,16);
	            				bFecha = true;
	            				
	            				
	            				sChequera = funciones.sacarDatoEntreComas(sRegistro, 1);
	            				if(sChequera.equals("NO DISPONIBLE"))
	            					continue sigueLeyendo;
	            				sChequera = funciones.eliminarCerosAlaIzquierda(sChequera.trim());
	            				if(sChequera.length() != 13)
	            				{
	            					sChequera = funciones.ponerCeros(sChequera, 11);
	            					sChequera = funciones.cadenaRight(sChequera, 9);
	            					sChequera = funciones.ponerCeros(sChequera, 11);
	            				}
	            				
	            				List<BarridoChequerasDto> list = new ArrayList<BarridoChequerasDto>();
	            				list = coinversionDao.consultarChequeraConcentradora(ConstantesSet.BANCOMER, sChequera);
	            				
	            				if(list.size() > 0){
	            					iNoEmpresa =list.get(0).getNoEmpresa();
	            					sChequera=list.get(0).getIdChequera();
	            				}
	            				else
	            					continue sigueLeyendo;
	            				list.clear();
	            				list = null;
	            				
	            				sFechaHora = sFecha;
	            				
	            				sDato = funciones.sacarDatoEntreComas(sRegistro, 5).replace(",", "");
	            				if(sDato.equals("NO DISPONIBLE"))
	            					continue sigueLeyendo;
	            				dSaldoChequera = Double.parseDouble(sDato);
	            				
	            				//checa si ya existe el movimiento en Saldo_chequera
	            				List<SaldoChequeraDto> listSaldo = new ArrayList<SaldoChequeraDto>();
	            				listSaldo = coinversionDao.consultarSaldoChequera(ConstantesSet.BANCOMER, sChequera, sFechaHora);
	            				if(listSaldo.size() <= 0){
    	            				iSecuencia = this.obtenerFolioReal("secuencia_saldo");
    	            				
    	            				SaldoChequeraDto dto = new SaldoChequeraDto();
    	            				dto.setNoEmpresa(iNoEmpresa);
    	            				dto.setIdBanco(ConstantesSet.BANCOMER);
    	            				dto.setIdChequera(sChequera);
    	            				dto.setSecuencia(iSecuencia);
    	            				dto.setSaldo(dSaldoChequera);
    	            				dto.setArchivo(sArchivo);
    	            				
    	            				coinversionDao.insertaSaldoChequera(dto, sFechaHora);
    	            				iImportados = iImportados + 1;
    	            			}
	            				listSaldo.clear();
	            				listSaldo = null;
	            				bTransaccion = false;
	            				
            				} //if(funciones.isDate(sFecha, true) && sFecha.length() == 19)...
            			} //while(archivoLeer.hasNextLine())...
		            
		            archivoLeer.close();
		            archivoLeer = null;
		            if(bArchivoOk)
		            {
		            	sArchivo = nombresArchivo[arch].getName().trim().replace(".csv", ".old");
		            	this.renombrarArchivo(nombresArchivo[arch], ".csv", ".old");
		            }
		            
		            if(bFecha == false)
		            	mensajes.add("El archivo " + sArchivo + " No corresponde a saldos Bancomer");
		            	
		            if(iImportados > 0)
		            	this.guardarArchivoImportado(sArchivo, ConstantesSet.BANCOMER);
		            	
		            arch ++;
			    } //while (arch < nombresArchivo.length)...
		    
		    mensajes.add("Chequeras Bancomer importadas: " + iImportados);
		    
		}catch(Exception e){
			mensajes.add("Ocurrio un error en la lectura del archivo " + sArchivo);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:LeerLayout, C:ImportarSaldosChequerasBusiness, M:importarSaldosBancomer");
		}
		return mensajes;
	}
	
	/**
	 * Function importar_saldos_AZTECA
	 * @param sRutaRegreso
	 * @return
	 */
	public List<String> importarSaldosAzteca(String sRutaRegreso){
		String sFechaHoraArchivo = "";
		String sAzteca = "";
		List<String> mensajes = new ArrayList<String>();
		this.inicializar();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			bTransaccion = false;
		    sAzteca = sRutaRegreso + "\\saldos_Azteca";
		    if (!sAzteca.equals(rutaActual))
				System.setProperty("user.dir", sAzteca);   //nos cambiamos al directorio de archivos de movimientos
		    
		    //toma el primer archivo .csv del directorio en el que est� posicionado
		    iImportados = 0;
		    nombresArchivo = busquedaArchivos.obtenerNombreArchivos(".csv");
		    
		    siguienteArchivo:
			    while (arch < nombresArchivo.length){   //hasta que no se encuentre archivos con extensi�n csv
		            bArchivoOk = true;
		            bFecha = false;
		            archivoLeer = new Scanner(nombresArchivo[arch]);
		            sArchivo = nombresArchivo[arch].getName();
		            sFechaHoraArchivo = funciones.obtenerFechaArchivo(sAzteca);
		            sExisteArchivo = this.existeArchivo(sArchivo, "Banco Azteca");
		            
		            if(!sExisteArchivo.equals(""))
		            {
		            	mensajes.add(""+sExisteArchivo);
		            	sArchivo = nombresArchivo[arch].getName().trim().replace(".csv", ".old");
		            	this.renombrarArchivo(nombresArchivo[arch], ".csv", ".old");
		            	arch ++;
		            	continue siguienteArchivo;
		            }
		            
		            if(archivoLeer == null)
	        			break;
		            
		            sRegistro = archivoLeer.nextLine(); //primer renglon de titulos
		            
		            sigueLeyendo:
            			while(archivoLeer.hasNextLine())
            			{
            				sRegistro = archivoLeer.nextLine();
            				sRegistro = sRegistro.replace("'", " ");
            				
            				sChequera = funciones.sacarDatoEntreComas(sRegistro, 1).trim();
            				sChequera = sChequera.replace(" ", "");
            				
            				if(sChequera.length() == 14)
            				{
            					bFecha = true;
		                    
            					List<BarridoChequerasDto> list = new ArrayList<BarridoChequerasDto>();
	            				list = coinversionDao.consultarChequeraConcentradora(ConstantesSet.AZTECA, sChequera);
	            				
	            				if(list.size() > 0)
	            					iNoEmpresa =list.get(0).getNoEmpresa();
	            				else
	            					continue sigueLeyendo;
	            				list.clear();
	            				list = null;
	            				
	            				sDato = funciones.sacarDatoEntreComas(sRegistro, 5).replace(",", "");
	            				dSaldoChequera = Double.parseDouble(sDato);
	            				sFechaHora = funciones.ponerFormatoDate(globalSingleton.getFechaHoy());
		                               
		                    //checa si ya existe el movimiento en Saldo_chequera
	            				List<SaldoChequeraDto> listSaldo = new ArrayList<SaldoChequeraDto>();
	            				listSaldo = coinversionDao.consultarSaldoChequera(ConstantesSet.AZTECA, sChequera, sFechaHora);
	            				if(listSaldo.size() <= 0){
    	            				iSecuencia = this.obtenerFolioReal("secuencia_saldo");
    	            				
    	            				sFechaHora = sFechaHoraArchivo;
    	            				
    	            				SaldoChequeraDto dto = new SaldoChequeraDto();
    	            				dto.setNoEmpresa(iNoEmpresa);
    	            				dto.setIdBanco(ConstantesSet.AZTECA);
    	            				dto.setIdChequera(sChequera);
    	            				dto.setSecuencia(iSecuencia);
    	            				dto.setSaldo(dSaldoChequera);
    	            				dto.setArchivo(sArchivo);
    	            				
    	            				coinversionDao.insertaSaldoChequera(dto, sFechaHora);
    	            				iImportados = iImportados + 1;
    	            			}
	            				listSaldo.clear();
	            				listSaldo = null;
		                    
	            				bTransaccion = false;
            				} //if(sChequera.length() == 14)...
            			} //while(archivoLeer.hasNextLine())...
		            
		            archivoLeer.close();
		            archivoLeer = null;
		            
		            if(bArchivoOk)
		            {
		            	sArchivo = nombresArchivo[arch].getName().trim().replace(".csv", ".old");
		            	this.renombrarArchivo(nombresArchivo[arch], ".csv", ".old");
		            }
		            
		            if(bFecha == false)
		            	mensajes.add("El archivo " + sArchivo + " No corresponde a saldos Azteca");
		            
		            if(iImportados > 0)
		            	this.guardarArchivoImportado(sArchivo, ConstantesSet.AZTECA);
		            
		            arch ++;
			    } //while (arch < nombresArchivo.length)...
		    
		    mensajes.add("Chequeras Banco Azteca importadas: " + iImportados);
			
		}catch(Exception e){
			mensajes.add("Ocurrio un error en la lectura del archivo " + sArchivo);
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:LeerLayout, C:ImportarSaldosChequerasBusiness, M:importarSaldosAzteca");
		}
		return mensajes;
	}
	
	public String existeArchivo(String sArchivo, String sBanco){
		int iExiste = -1;
		String sExiste = "";
		try{
			iExiste = coinversionDao.consultaExistenciaArchivo(sArchivo);
			if(iExiste <= 0)
				sExiste = "";
	        else
	        	sExiste = "El archivo " + sArchivo + " de " + sBanco + " ya fue importado";
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:LeerLayout, C:ImportarSaldosChequerasBusiness, M:existeArchivo");
		}
		return sExiste;
	}
	
	public void guardarArchivoImportado(String sArchivo, int iBanco){
		String sFecha;
		sFecha = funciones.ponerFechaSola(globalSingleton.getFechaHoy());
    	
    	SaldoChequeraDto dto = new SaldoChequeraDto();
    	dto.setArchivo(sArchivo);
    	dto.setIdBanco(iBanco);
    	coinversionDao.insertaArchivosSaldos(dto, sFecha);
	}
	
	public int obtenerFolioReal(String tipoFolio){
		int folio = 0;
		try{
			coinversionDao.actualizarFolioReal(tipoFolio);
			folio = coinversionDao.seleccionarFolioReal(tipoFolio);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:LeerLayout, C:ImportarSaldosChequerasBusiness, M:obtenerFolioReal");
			return 0;
		}
		return folio;
	}
	
	public File renombrarArchivo(File dato, String extensionActual, String extensionNueva){
		try{
			if(!dato.renameTo(new File(dato.getAbsolutePath().replace(extensionActual, extensionNueva))))
				logger.info("Fallo en renombrar el archivo: " + dato.getAbsolutePath());
			return dato;
		}catch(Exception e){
       		bitacora.insertarRegistro(new Date().toString()+ " "+ Bitacora.getStackTrace(e)
					+ "P:LeerLayout, C:ImportarSaldosChequerasBusiness, M:renombrarArchivo");
       		return null;
       	}
	}
	
	public static void main(String []args){
		XmlBeanFactory beanFactory = new XmlBeanFactory(new FileSystemResource("C:\\proyectos\\webset\\workspace\\SET\\WebRoot\\WEB-INF\\applicationContext.xml"));
		ImportarSaldosChequerasBusiness obj = (ImportarSaldosChequerasBusiness) beanFactory.getBean("importarSaldosChequerasBusiness");
		GlobalSingleton globalSingleton;
		globalSingleton = GlobalSingleton.getInstancia();
		String sRutaRegreso = globalSingleton.obtenerValorConfiguraSet(201);
		obj.importarSaldosBancomer(sRutaRegreso);
	}
	

	public CoinversionDao getCoinversionDao() {
		return coinversionDao;
	}

	public void setCoinversionDao(CoinversionDao coinversionDao) {
		this.coinversionDao = coinversionDao;
	}
}
