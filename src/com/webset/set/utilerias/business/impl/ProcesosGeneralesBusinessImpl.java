package com.webset.set.utilerias.business.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.AxisFault;
import org.apache.log4j.Logger;

import com.webset.set.bancaelectronica.dao.EnvioTransferenciasDao;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dao.ProcesosGeneralesDao;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.service.ProcesosGeneralesService;
import com.webset.set.utileriasmod.dto.ProcesosGeneralesDto;

import mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_OBContabilidadElectronica;
import mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_ResponseContabilidadResponse;
import mx.com.gruposalinas.ContabilidadElectronica.SOS_ContabilidadElectronicaBindingStub;
import mx.com.gruposalinas.ContabilidadElectronica.SOS_ContabilidadElectronicaServiceLocator;

public class ProcesosGeneralesBusinessImpl implements ProcesosGeneralesService{
	ProcesosGeneralesDao objProcesosGeneralesDao;
	Bitacora bitacora;
	Funciones funciones = new Funciones();
	private EnvioTransferenciasDao envioTransferenciasDao;
	
	private static Logger logger = Logger.getLogger(ConsultasGenerales.class);
	
	/**
	 * 
	 */
	public List<ProcesosGeneralesDto> llenaGrid(){
		List<ProcesosGeneralesDto> recibeDatos = new ArrayList<ProcesosGeneralesDto>();
		
		try{
			recibeDatos = objProcesosGeneralesDao.llenaGrid();				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesBusinessImpl, M: llenaGrid");
		}return recibeDatos;
	}
	
	/**
	 * 
	 */
	public int validaEstatus(){		
		return objProcesosGeneralesDao.validaEstatus();
	}
	
	/**
	 * 
	 */
	public int actualizaEstatusSist2(){
		int recibeEntero = 0;
		int estatus = 0;
		recibeEntero = objProcesosGeneralesDao.validaEstatusSist2();
		
		if (estatus != 0){
			recibeEntero = objProcesosGeneralesDao.actualizaEstatus(estatus);
			objProcesosGeneralesDao.actualizaEstatusSist2("uno");
		}		
		return recibeEntero;
	}
	
	public int validaUsuariosConectados(){
		int recibeEntero = 0;
		try{
			recibeEntero = objProcesosGeneralesDao.validaUsuariosConectados();			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesBusinessImpl, M: validaUsuariosConectados");
		}return recibeEntero;
	}
	
	public String correCierre(int noUsuario, String fecHoy){
		String recibeDatos = "";
		String mensaje = "";
		//int recibeEntero = 0;
		Map<String, Object> mapaHistorico = new HashMap<String, Object>();			
		
		try{
			
			recibeDatos = objProcesosGeneralesDao.respaldoBD(258);
			
			if (recibeDatos.equals("SI")){				
				//Esto es para generar el NETEO
				mensaje = generaNeteo(fecHoy, noUsuario);
				
				if (!mensaje.equals(""))
					return mensaje;
			}

				
			//Aqui se manda llamar el historico para el cierre
			mapaHistorico = objProcesosGeneralesDao.correHistorico();
			
			if (mapaHistorico.size() > 0){
				
				objProcesosGeneralesDao.actualizaEstatusSist2("uno");					
					
				mensaje = "Cierre terminado con �xito";
			}
			else
				mensaje = "Ocurrio un error durante el proceso";
			
			
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesBusinessImpl, M: correCierre");
		}return mensaje;
	}
	
	public void correIndicadorBancario(){
		String result = "";
		String[] indicadoresBanc;
		String mensaje = "";
		try
		{
			//Se crea la URL, se abre la conexi�n y se obtiene el resultado.
			//URL urlForex = new URL("http://www.exchangeratewidget.com/converter.php?l=es&f=&t=USDMXN,EURMXN,GBPMXN,JPYMXN,CHFMXN,CADMXN,AUDMXN,&a=1&d=FFFFFF&n=FFFFFF&o=000000&v=11");
			URL urlForex = new URL("http://www.exchangeratewidget.com/converter.php?l=es&f=&t=USDMXN,EURMXN,GBPMXN,VEFMXN,&a=1&d=FFFFFF&n=FFFFFF&o=000000&v=11");
			URLConnection myURLConnection = urlForex.openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));
			
			//Se procesa el resultado.
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				result += inputLine;
			}
			br.close();
			result = result.replaceAll("Tasas de Cambio:", "");
			//Se recupera el resultado desde marquee a marquee.
			result = result.substring(result.indexOf("<marquee"), result.lastIndexOf("<span"));
			result = result.replaceAll("\"","'");
			result = result.replace("<marquee behavior='scroll' scrollamount='2' direction='left' onmouseover='stop()' onmouseout='start()'>", "");
			result = result.replace("<span  style=' padding-left:10px;'> </span><span  style='padding-left:10px;'>", "");
			result = result.replace("</span> = <span  style='padding-left:2px;'>", "#");
			result = result.replace("</span><span  style='padding-left:10px;'>", "#");
			result = result.replace("</span> = <span  style='padding-left:2px;'>", "#");
			result = result.replace("</span><span  style='padding-left:10px;'>", "#");
			result = result.replace("</span> = <span  style='padding-left:2px;'>", "#");
			result = result.replace("</span><span  style='padding-left:10px;'>", "#");
			result = result.replace("</span> = <span  style='padding-left:2px;'>", "#");
			result = result.replace("</span>", "");
			System.out.println(result);
			indicadoresBanc = result.split("#");

			int res =objProcesosGeneralesDao.insertaValorDivisa(indicadoresBanc);
			if(res>0){
				mensaje="Indicadores insertados con exito";
			}else{ 
				mensaje="Ocurrio un error al actualizar los indicadores";
			}
			System.out.println(mensaje);
			/*result += "<span  style=\"padding-left:10px;\">CET</span> = <span  style=\"padding-left:2px;\">0.1294</span>";
			result += "<span  style=\"padding-left:10px;\">LIB3</span> = <span  style=\"padding-left:2px;\">14.918</span>";
			result += "<span  style=\"padding-left:10px;\">TIIE</span> = <span  style=\"padding-left:2px;\">11.984</span>";
			result += "</marquee>";*/
		}
		catch(MalformedURLException mu){
			System.err.println(mu);
		}
		catch(IOException io){
			System.err.println(io);
		}
	}
	
	
	public String generaNeteo(String fecHoy, int noUsuario){
		GeneradorDto generadorDto = new GeneradorDto();
		String mensaje = "";
		double pdSaldoCoinversion = 0;
		double pdSaldoPrestamos = 0;
		double pdSaldoNeto = 0;
		double pdAjustePrestamo = 0;
		double pdAjusteCoinversion = 0;	
		generadorDto.setFolMovi(0);
		generadorDto.setFolDeta(0);
		
		
		int folioParam2 = 0;
		int recibeEntero = 0;
		List<ProcesosGeneralesDto> recibeDatos = new ArrayList<ProcesosGeneralesDto>();
		ProcesosGeneralesDto objDto = new ProcesosGeneralesDto();
		Map<String, Object> mapa = new HashMap<String, Object>();
		
		
		
		
		
		try{			
			recibeDatos = objProcesosGeneralesDao.procesoNeteo(fecHoy, 0);
			
			if (recibeDatos.size() > 0){
				for (int i = 0; i <= recibeDatos.size(); i++)
				{
					pdSaldoCoinversion = recibeDatos.get(i).getSdoCoinversion();
					pdSaldoPrestamos = recibeDatos.get(i).getSdoCredito();
					pdSaldoNeto = pdSaldoCoinversion - pdSaldoPrestamos;
					
					if (pdSaldoNeto > 0){
						// El credito debe quedar en 0 y la coinversion igual al saldo neteado						 * 
						pdAjustePrestamo = -1 * pdSaldoPrestamos;
						pdAjusteCoinversion = pdSaldoNeto - pdSaldoCoinversion;
					}
					else if (pdSaldoNeto < 0){
						// coinversion en 0 y credito igual al saldo neteado			            
			            pdAjustePrestamo = Math.abs(pdSaldoNeto) - pdSaldoPrestamos;
			            pdAjusteCoinversion = -1 * pdSaldoCoinversion;
					}
					else{
						//coinversion en 0 y credito en 0
						pdAjustePrestamo = -1 * pdSaldoPrestamos;
			            pdAjusteCoinversion = -1 * pdSaldoCoinversion;
					}
					
					generadorDto.setFolParam(objProcesosGeneralesDao.obtieneFolio("no_folio_param"));					
					objProcesosGeneralesDao.actualizaFolio("no_folio_param");
					
					folioParam2 = objProcesosGeneralesDao.obtieneFolio("no_folio_param");					
					objProcesosGeneralesDao.actualizaFolio("no_folio_param");
					
					generadorDto.setEmpresa(recibeDatos.get(i).getNoEmpresa());	
					
					objDto.setFolioParam(generadorDto.getFolParam());
					objDto.setFolioParam2(folioParam2);
					objDto.setNoCuentaEmp(recibeDatos.get(i).getNoCuentaEmp());
					objDto.setIdChequera("");
					objDto.setIdBanco(0);
					objDto.setAjustePrestamo(pdAjustePrestamo);
					objDto.setFechaHoy(fecHoy);
					objDto.setUsuarioAlta(1);
					objDto.setIdDivisa(recibeDatos.get(i).getIdDivisa());
					objDto.setIdBancoBenef(0);
					objDto.setIdChequeraBenef(""); 					
					objDto.setIdCaja(0);					
					objDto.setTipoCambio(1);
														
					
					//Folio para grupo
					if (pdAjustePrestamo != 0) {
						if (pdAjustePrestamo < 0) {
							objDto.setNoEmpresa(recibeDatos.get(i).getNoEmpresa());
							objDto.setTipoOperacion(3709);
							objDto.setConcepto("PAGO CREDITO, PROCESO DE NETEO");
							objDto.setCoinversora(recibeDatos.get(i).getCoinversora());
							objDto.setSecuencia(1);
							
							//Inserta en parametro
							recibeEntero = objProcesosGeneralesDao.insertaEnParametro(objDto);
							
							if (recibeEntero > 0){
								//Llamada al generador								
								mapa = objProcesosGeneralesDao.llamaGenerador(generadorDto);							
									
								if (!mapa.get("result").equals("0"))
									mensaje = "Error al Ejecutar el Generador";									
							}								 
						}
						else {
							objDto.setNoEmpresa(recibeDatos.get(i).getCoinversora());
							objDto.setTipoOperacion(3708);
							objDto.setConcepto("PRESTAMO, PROCESO DE NETEO");
							objDto.setCoinversora(recibeDatos.get(i).getNoEmpresa());
							objDto.setSecuencia(1);
							
							//Inserta en parametro							
							recibeEntero = objProcesosGeneralesDao.insertaEnParametro(objDto);
							
							
							//Este es el segundo insert solo cambian estos datos
							objDto.setNoEmpresa(recibeDatos.get(i).getNoEmpresa());
							objDto.setCoinversora(recibeDatos.get(i).getCoinversora());
							objDto.setSecuencia(2);
							
							//Inserta en parametro lo unico que cambia es la secuencia
							recibeEntero = objProcesosGeneralesDao.insertaEnParametro(objDto);
							
							if (recibeEntero > 0){
								//Llamada al generador								
								mapa = objProcesosGeneralesDao.llamaGenerador(generadorDto);							
									
								if (!mapa.get("result").equals("0"))
									mensaje = "Error al Ejecutar el Generador";									
							}
						}						
					}//Termina el if (pdAjustePrestamo != 0)
					
					if (pdAjusteCoinversion != 0){
						if (pdAjusteCoinversion < 0){
							objDto.setNoEmpresa(recibeDatos.get(i).getCoinversora());
							objDto.setTipoOperacion(8200);
							objDto.setConcepto("RETIRO, PROCESO DE NETEO");
							objDto.setCoinversora(recibeDatos.get(i).getNoEmpresa());
							objDto.setSecuencia(1);
							objDto.setAjustePrestamo(pdAjusteCoinversion);
							
							//Inserta en parametro							
							recibeEntero = objProcesosGeneralesDao.insertaEnParametro(objDto);
							
							objDto.setSecuencia(2);
							//Inserta en parametro lo unico que cambia es la secuencia
							recibeEntero = objProcesosGeneralesDao.insertaEnParametro(objDto);
							
							if (recibeEntero > 0){
								//Llamada al generador								
								mapa = objProcesosGeneralesDao.llamaGenerador(generadorDto);							
									
								if (!mapa.get("result").equals("0"))
									mensaje = "Error al Ejecutar el Generador";									
							}
						}
						else{
							objDto.setNoEmpresa(recibeDatos.get(i).getCoinversora());
							objDto.setTipoOperacion(8201);
							objDto.setConcepto("APORTACION, PROCESO DE NETEO");
							objDto.setCoinversora(recibeDatos.get(i).getNoEmpresa());
							objDto.setSecuencia(1);
							objDto.setAjustePrestamo(pdAjusteCoinversion);
							
							//Inserta en parametro							
							recibeEntero = objProcesosGeneralesDao.insertaEnParametro(objDto);
							
							objDto.setSecuencia(2);
							//Inserta en parametro lo unico que cambia es la secuencia
							recibeEntero = objProcesosGeneralesDao.insertaEnParametro(objDto);
							
							if (recibeEntero > 0){
								//Llamada al generador								
								mapa = objProcesosGeneralesDao.llamaGenerador(generadorDto);							
									
								if (!mapa.get("result").equals("0"))
									mensaje = "Error al Ejecutar el Generador";									
							}
						}							
					}					
				}
			}		
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C:ProcesosGeneralesBusinessImpl, M: generaNeteo");
		}return mensaje;
	}
	
	public String obtieneFecha(){
		return objProcesosGeneralesDao.obtieneFecha();
	}
	
	public String respaldoBD(int indice){
		return objProcesosGeneralesDao.respaldoBD(indice);
	}
	
	public int generaRespaldoBD(String fecHoy){
		int recibeEntero = 0;
		
		try{		
			//Se genera el respaldo de la Base de Datos aqui	
			recibeEntero = objProcesosGeneralesDao.creaRespaldoBD("webset_local", " Cierre " + fecHoy + ".bak");	
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesBusinessImpl, M: generaRespaldoBD");
		}return recibeEntero;		
	}
	
	/**
	 * Metodo para ejecutar el cierre del d�a de forma autom�tica.
	 * Se ejecuta desde el CRON.
	 * Se considera siempre el idUsuario=2 y la fecha del d�a.
	 * No realiza backup ni ejecuta Neteo.
	 * 
	 * @return String mensaje
	 */
	public void correCierreAuto(){
		logger.debug("Entra: correCierreAuto");
		InetAddress localHost = null;
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		String miIP = localHost.getHostAddress();
		String ipConf = objProcesosGeneralesDao.configuraSet(4848);
		
		System.out.println(miIP.equals(ipConf) ? "si":"no");
		
		System.out.println(localHost.getHostName());
		System.out.println(localHost.getHostAddress());
		System.out.println("Entra: correCierreAuto");
		String mensaje = "";
		
		try
		{
			if (miIP.equals(ipConf)) {
				//Date hoy = new Date();
				//String fecha = objProcesosGeneralesDao.obtieneFecha();
				//ring fechaHoy = funciones.obtenerAnio(hoy) + "-" + funciones.obtenerMes(hoy) + "-" + funciones.obtenerDia(hoy);
				//System.out.println("fecha base datos: " + fecha + " fecha hoy: " + fechaHoy);
				//System.out.println(fecha.compareTo(fechaHoy));
				//if (fecha.compareTo(fechaHoy) < 0) {
					 // for (;fecha.compareTo(fechaHoy) < 0;) {
						  Map<String, Object> mapaHistorico = objProcesosGeneralesDao.correHistorico();
						  if (mapaHistorico.size() > 0) mensaje = "Cierre terminado con �xito";
						  else mensaje = "Ocurrio un error durante el proceso";
					  //}
				//}
			} else {
				InetAddress ping; 
				 // Ip de la mᱵina remota 
				try { 
					ping = InetAddress.getByName(ipConf);
					if(ping.isReachable(5000)){ 
						System.out.println(ipConf+" - responde!"); 
					}else { 
						//Date hoy = new Date();
						//String fecha = objProcesosGeneralesDao.obtieneFecha().substring(0, 10);
						//String fechaHoy = funciones.fechaString(hoy);
						//System.out.println("fecha base datos: " + fecha + " fecha hoy: " + fechaHoy);
						//System.out.println(fecha.compareTo(fechaHoy));
						//if (fecha.compareTo(fechaHoy) < 0) {
							 // for (;fecha.compareTo(fechaHoy) < 0;) {
								  Map<String, Object> mapaHistorico = objProcesosGeneralesDao.correHistorico();
								  if (mapaHistorico.size() > 0) mensaje = (int) mapaHistorico.get("vResult") == 0 ? "Cierre finalizado con exito" : mapaHistorico.get("vMensaje") + "";
								  else mensaje = "Ocurrio un error durante el proceso";
								 // fecha = objProcesosGeneralesDao.obtieneFecha().substring(0, 10);
							//  }
						//}
					}
				} catch (IOException ex) { System.out.println(ex); } 
				
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesBusinessImpl, M: correCierreAuto");
			logger.error("Error en: correCierreAuto" + e.toString());
			System.out.println("Error en: correCierreAuto" + e.toString());
		}
		logger.debug("Sale: correCierreAuto : ["+mensaje+"]");
		System.out.println("Sale: correCierreAuto : ["+mensaje+"]");
	}
	
	public void importador(){
		InetAddress localHost = null;
		try {
			localHost = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		
		String miIP = localHost.getHostAddress();
		String ipConf = objProcesosGeneralesDao.configuraSet(4848);
		
		String ejecutar = objProcesosGeneralesDao.configuraSet(4949);
		
		System.out.println(miIP.equals(ipConf) ? "si":"no");
		
		System.out.println(localHost.getHostName());
		System.out.println(localHost.getHostAddress());
		System.out.println("Entra: importador");
		String mensaje = "";
		
		try {
			if (miIP.equals(ipConf) &&
					ejecutar.toUpperCase().equals("SI")) {
				Map<String, Object> resultado = objProcesosGeneralesDao.importaProv();
				
				if (resultado.size() > 0) {
					if ((Boolean)resultado.get("Error")) {
						mensaje = "Ocurrio un error durante el proceso de datos maestros";
					} else {
						mensaje = "Exito durante el proceso";
					}
				} else
					mensaje = "Exito durante el proceso";
				
				resultado = objProcesosGeneralesDao.importaPasivos();
				
				if (resultado.size() > 0) {
					if ((Boolean)resultado.get("Error")) {
						mensaje = "Ocurrio un error durante el proceso de importacion de pasivos";
					} else {
						mensaje = "Exito durante el proceso";
					}
				} else
					mensaje = "Exito durante el proceso";
			}  else {
				InetAddress ping; 
				 // Ip de la mᱵina remota 
				try { 
					ping = InetAddress.getByName(ipConf);
					if(ping.isReachable(5000)){ 
						System.out.println(ipConf+" - responde!"); 
					}else if (ejecutar.toUpperCase().equals("SI")) {
						Map<String, Object> resultado = objProcesosGeneralesDao.importaProv();
						
						if (resultado.size() > 0) {
							if ((Boolean)resultado.get("Error")) {
								mensaje = "Ocurrio un error durante el proceso de datos maestros";
							} else {
								mensaje = "Exito durante el proceso";
							}
						} else
							mensaje = "Exito durante el proceso";
						
						resultado = objProcesosGeneralesDao.importaPasivos();
						
						if (resultado.size() > 0) {
							if ((Boolean)resultado.get("Error")) {
								mensaje = "Ocurrio un error durante el proceso de importacion de pasivos";
							} else {
								mensaje = "Exito durante el proceso";
							}
						} else
							mensaje = "Exito durante el proceso";						
					}
						
				} catch (IOException ex) { System.out.println(ex); } 
				
			}
		} catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: ProcesosGeneralesBusinessImpl, M: importador");
			
			System.out.println("Error en: importador" + e.toString());
		}
		
		System.out.println("Sale: importador : ["+mensaje+"]");
	}
	
	public void bloqueaUsuarios() {
		objProcesosGeneralesDao.bloqueaUsuarios();
	}
	
	//Getters and Setters
	public ProcesosGeneralesDao getObjProcesosGeneralesDao() {
		return objProcesosGeneralesDao;
	}

	public void setObjProcesosGeneralesDao(
			ProcesosGeneralesDao objProcesosGeneralesDao) {
		this.objProcesosGeneralesDao = objProcesosGeneralesDao;
	}
	
	public void mandarAContabilidadElectronica(){
		//String resultado = "Error al procesar el registro";
		try {
			List<DT_ContabilidadElectronica_OBContabilidadElectronica>datos= 
			objProcesosGeneralesDao.obtenerDatosContabilidadElectronica();
			if (datos!=null && !datos.isEmpty()) {
				Map<String, Object> respuestas= 
						mandarContabilidadASap(datos.toArray(new DT_ContabilidadElectronica_OBContabilidadElectronica
								[datos.size()]));
				if (respuestas.get("estatus").equals(true)) {
					DT_ContabilidadElectronica_ResponseContabilidadResponse[] respuestasArreglo =
							(DT_ContabilidadElectronica_ResponseContabilidadResponse[])
							respuestas.get("respuestaWS");
					
					if (respuestasArreglo != null && respuestasArreglo.length !=0) {
						for (int i = 0; i < respuestasArreglo.length; i++) {
							objProcesosGeneralesDao.actualizarMovimiento(
									respuestasArreglo[i],
									datos.get(i)
									);
						}

					} else {
						//resultado= "No se obtuvo respuesta de SAP";
					}
							
				} else {
					//resultado= respuestas.get("mensaje").toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: ProcesosGeneralesBusinessImpl, M: mandarAContabilidadElectronica");
		}
		
	}

	private Map<String, Object> mandarContabilidadASap(
			DT_ContabilidadElectronica_OBContabilidadElectronica[] array) {
		Map<String, Object> resultado = new HashMap<String, Object>();
		resultado.put("estatus", false);
		resultado.put("mensaje", "Error al procesar el registro");
		try {
			SOS_ContabilidadElectronicaServiceLocator service=
					new SOS_ContabilidadElectronicaServiceLocator();
			SOS_ContabilidadElectronicaBindingStub stub=
					new SOS_ContabilidadElectronicaBindingStub(
							new URL(service.getHTTP_PortAddress()), service);
			stub.setUsername(
					objProcesosGeneralesDao.configuraSet(
							ConstantesSet.USERNAME_WS_CONTABILIDAD));
			stub.setPassword(
					objProcesosGeneralesDao.configuraSet(
							ConstantesSet.PASSWORD_WS_CONTABILIDAD));
			DT_ContabilidadElectronica_ResponseContabilidadResponse[] resp=
					stub.SOS_ContabilidadElectronica(array);
			
			resultado.put("estatus", true);
			resultado.put("respuestaWS", resp);
			
		} catch ( MalformedURLException e1) {
			resultado.put("mensaje","No se pudo conectar a SAP.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Consultas, C:ConsultasBusiness, M:mandarContabilidadASap");
		} catch (AxisFault e1) {
			resultado.put("mensaje","SAP ha tenido un problema al procesar los datos.");
			e1.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + e1.toString() 
			+ "P:Consultas, C:ConsultasBusiness, M:mandarContabilidadASap");
		} catch (Exception e) {
			resultado.put("mensaje","Error al procesar el registro");
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Consultas, C:ConsultasBusiness, M:mandarContabilidadASap");
		} return resultado;
		
	}
	


	
	public EnvioTransferenciasDao getEnvioTransferenciasDao() {
		return envioTransferenciasDao;
	}
	public void setEnvioTransferenciasDao(EnvioTransferenciasDao envioTransferenciasDao) {
		this.envioTransferenciasDao = envioTransferenciasDao;
	}
}
