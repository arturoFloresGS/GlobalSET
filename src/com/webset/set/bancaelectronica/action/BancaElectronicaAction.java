package com.webset.set.bancaelectronica.action;

//UTIL
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

//EXTJS DIRECT 
import com.softwarementors.extjs.djn.config.annotations.DirectFormPostMethod;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
//APP
import com.webset.set.bancaelectronica.business.AdministradorArchivosBusiness;
import com.webset.set.bancaelectronica.business.BancaElectronicaBusiness;
import com.webset.set.bancaelectronica.dao.AdministradorArchivosDao;
import com.webset.set.bancaelectronica.dto.ParametroLayoutDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ReferenciaEncDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.utils.tools.Utilerias;

/**
 * 
 * @author Sergio Vaca
 *
 */
public class BancaElectronicaAction {
	private BancaElectronicaBusiness bancaElectronicaBusiness;
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private Funciones funciones = new Funciones();
	private static Logger logger = Logger.getLogger(BancaElectronicaAction.class);
	
	AdministradorArchivosBusiness objAdministradosBusiness;
	GlobalSingleton globalSingleton;
	
	/**
	 * 
	 * @return List<ComunDto>
	 * 
	 * Obtiene los bancos activos
	 * 
	 * Public Function FunSQLCombo367() As ADODB.Recordset
	 */
	@DirectMethod
	public List<ComunDto> seleccionarBancosActivos(){
		List<ComunDto> datos = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			bancaElectronicaBusiness = (BancaElectronicaBusiness)contexto.obtenerBean("bancaElectronicaBusiness");
			datos = bancaElectronicaBusiness.seleccionarBancosActivos();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:BancaElectronicaAction, M:seleccionarBancosActivos");
		}
		return datos;
	}
	
	/**
	 * 
	 * @return List<ReferenciaEncDto>
	 * 
	 * Public Function FunSQLLevantaRefEnc() As ADODB.Recordset
    	Se llama en la forma frmTransIng
	 */
	@DirectMethod
	public List<ReferenciaEncDto> levantarRefEnc(){
		List<ReferenciaEncDto> datos = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			bancaElectronicaBusiness = (BancaElectronicaBusiness)contexto.obtenerBean("bancaElectronicaBusiness");
			datos = bancaElectronicaBusiness.levantarRefEnc();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:BancaElectronicaAction, M:levantarRefEnc");
		}
		return datos;
	}
	
	/**
	 * Consulta la tabla del configura_set
	 * 
	 * @param indice
	 * @return
	 */
	@DirectMethod
	public Retorno consultarConfiguraSet(int indice) {
		Retorno retorno = null;
		if(indice>0){
			try{
				if (Utilerias.haveSession(WebContextManager.get())) {
				bancaElectronicaBusiness = (BancaElectronicaBusiness)contexto.obtenerBean("bancaElectronicaBusiness");
				retorno = new Retorno();
				retorno.setValorConfiguraSet(bancaElectronicaBusiness.consultarConfiguraSet(indice));
				}
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
						+ "P:BE, C:BancaElectronicaAction, M:consultarConfiguraSet");
			}
		}
		return retorno;
	}
	
	/**
	 * Consulta la tabla del configura_set
	 * 
	 * @param indice
	 * @return
	 */
	@DirectMethod
	public List<Retorno> consultarConfiguraSetTodos() {
		List<Retorno> retorno = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			bancaElectronicaBusiness = (BancaElectronicaBusiness)contexto.obtenerBean("bancaElectronicaBusiness");
			retorno=bancaElectronicaBusiness.consultarConfiguraSet();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C:BancaElectronicaAction, M:consultarConfiguraSet");
		}
		return retorno;
	}
	
	/**
	 * 
	 * @param formParameters
	 * @param fileFields
	 * @return SubmitResult
	 * 
	 * Para leer todos los layout
	 */
	@DirectFormPostMethod 
	public SubmitResult leerLayout(Map<String, String> formParameters, Map<String, FileItem> fileFields ){
		boolean referenciaC = formParameters.get("referenciaF").equals("true");
		boolean referenciaN = formParameters.get("referenciaN").equals("true");
		int id = Integer.parseInt(formParameters.get("idUsuarioF"));
		int longitud = Integer.parseInt(formParameters.get("longitudF"));
		int vec[];
		SubmitResult result = new SubmitResult();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			vec = funciones.retornarNumeros(formParameters.get("clavesF"), ",");
			//logger.info(formParameters.get("clavesF")+"  "+formParameters.get("idUsuarioF"));
			AdministradorArchivosBusiness obj = (AdministradorArchivosBusiness) contexto.obtenerBean("administradorArchivosBusiness");
			ParametroLayoutDto dto = new ParametroLayoutDto();
			int op=0,z=0;
			int importados = 0;
			int total=0;
			String mensajes="";
			String mensajesUsuario="";
			obj.setLbReferenciaPorChequera(referenciaC);
			obj.setLbReferenciaNumerica(referenciaN);
			obj.setLiLongitudReferencia(longitud);
			obj.setIdUsuario(id);
			
			do{
				dto.setPsArchivos("");
				dto.setPlRegLeidos(0);
				dto.setPlRegSinChequera(0);
				dto.setPlRegImportados(0);
				dto.setPsChequerasInexistentes("");
				
				op = vec[z];
				
				switch(op){
					case ConstantesSet.BANAMEX:
						if(obj.leerBanamex(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexMismoDia(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexMismoDia(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexTEF(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexVersionInternet(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						if(obj.leerCitibankDls(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						break;
					case ConstantesSet.BANCOMER:
						/*if(obj.leerBancomer(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
							else
								obj.actualizarFechaBanca("", obj.BANCOMER);
						}*/
						obj.leeEdosCtaAuto();
						
						if(obj.leeNetCashC43(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
							else
								obj.actualizarFechaBanca("", obj.BANCOMER);
						}
						importados += dto.getPlRegImportados();
					    total += importados;
						importados=0;
						break;
					case ConstantesSet.SANTANDER:
						if(obj.leerSantander(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
					    importados += dto.getPlRegImportados();
					    
					    dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
					    
						if(obj.leerSantanderBuzon(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
					    
						if(obj.leerSantanderTodasChequeras(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
					    importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						break;
					case ConstantesSet.BITAL:
						/*if (obj.leerHSBCVersionOficial(dto)) {
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.HSBC);
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if (obj.leerHSBCTodasColumnas(dto))
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.HSBC);
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						if (importados > 0)
							obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if (obj.leerHSBCTodasColumnasTodasChequeras(dto))
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						importados += dto.getPlRegImportados();
						
						if (importados > 0)
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
						total+=importados;
						importados=0;
						*/
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if (obj.leerHSBCGNP(dto))
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						importados += dto.getPlRegImportados();
						
						if (importados > 0)
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
						total+=importados;
						importados=0;
						break;
					case ConstantesSet.CITIBANK_DLS:
						obj.leerCitibankDls(dto);
						importados += dto.getPlRegImportados();
						total+=importados;
						importados=0;
						break;
					case ConstantesSet.BANORTE:
						if(obj.leeBanortePipe(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANORTE);
							else
								obj.actualizarFechaBanca("", obj.BANORTE);
						}
						importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						break;
					case ConstantesSet.INVERLAT:
						if(obj.leeInverlat(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.INVERLAT);
							else
								obj.actualizarFechaBanca("", obj.INVERLAT);
						}
						importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						break;
				}
				z++;
			}while(z<vec.length);
			
			for (int p = 0; p < obj.mensajes.size(); p++)
				if (obj.mensajes.get(p)!=null&&!obj.mensajes.get(p).equals(""))
					mensajes += obj.mensajes.get(p) + "\n";
			mensajes += "\nTotal Importados: " + total;
			if(obj.mensajesUsuario.size()>1){
				mensajesUsuario += "\n\nMesanjes de Usuario \n";
				for (int i = 0; i < obj.mensajesUsuario.size(); i++)
					mensajesUsuario += obj.mensajesUsuario.get(i) + "\n";
			}
			if(mensajes!=null || mensajesUsuario!=null || !mensajes.equals("") || !mensajesUsuario.equals("")){
				result.errors = new HashMap<String, String>();
				result.success=true;
				result.mensajes=mensajes;
				result.mensajesUsuario=mensajesUsuario;
				result.total=total;
			}
			}
		}catch(Exception e){
			/*bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:BE, C: BancaElectronicaAction, M:leerLayout");*/
			result.success=false;
			e.printStackTrace();
		}
		return result;
	}
	
	//Funcion para la lectura del MT940 de todos los bancos
	@DirectMethod
	public String lecturaMT940(String fechaHoy)
	{			
		String cadena = "";
		//boolean respuesta = false;
		try
		{		
			if (Utilerias.haveSession(WebContextManager.get())) {
				objAdministradosBusiness = (AdministradorArchivosBusiness)contexto.obtenerBean("administradorArchivosBusiness");
				cadena = objAdministradosBusiness.lecturaMT940(fechaHoy);
						
				if (!cadena.equals("") && !cadena.equals("Error en lectura"))
					return cadena;
				else
					cadena = "No existe información.";
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:BancaElectronicaAction, M:lecturaMT940");
			cadena = "Excepción al leer archivos.";
		}
		return cadena;
	}
	
	@DirectMethod
	public String eliminaArchivos()
	{			
		String cadena = "";
		//boolean respuesta = false;
		try
		{		
			if (Utilerias.haveSession(WebContextManager.get())) {
			objAdministradosBusiness = (AdministradorArchivosBusiness)contexto.obtenerBean("administradorArchivosBusiness");
			cadena = objAdministradosBusiness.eliminaArchivos();
					
			if (!cadena.equals("") && !cadena.equals("Error en lectura"))
				return cadena;
			else
				cadena = "No existe información";	
						
		}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:BancaElectronica, C:BancaElectronicaAction, M:eliminaArchivos");
			cadena = "";
		}return cadena;
	}
	
	@DirectMethod
	public int confirmacionAutomatica(String fecHoy){
		int respuesta = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
				objAdministradosBusiness= (AdministradorArchivosBusiness)contexto.obtenerBean("administradorArchivosBusiness");
				respuesta = objAdministradosBusiness.confirmacionAutomatica(fecHoy);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: BancaElectronica, C: BancaElectronicaAction, M: confirmacionAutomatica");
		}return respuesta;		
	}
	
	private static class SubmitResult { 
		@SuppressWarnings("unused") 
		public boolean success = true;
		@SuppressWarnings("unused") 
		public int total=0;
		@SuppressWarnings("unused") 
		public String mensajes="";
		@SuppressWarnings("unused") 
		public String mensajesUsuario="";
		@SuppressWarnings("unused") 
		public HashMap<String, String> errors; 
		@SuppressWarnings("unused") 
		public Map<String,String> debug_formPacket; 
	}
	
	
	@DirectFormPostMethod
	public SubmitResult leerLayoutProcedimiento(Map<String, String> formParameters, Map<String, FileItem> fileFields ){

		boolean referenciaC = formParameters.get("referenciaF").equals("true");
		boolean referenciaN = formParameters.get("referenciaN").equals("true");
		int id = Integer.parseInt(formParameters.get("idUsuarioF"));
		int longitud = Integer.parseInt(formParameters.get("longitudF"));
		int vec[];
		SubmitResult result = new SubmitResult();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			vec = funciones.retornarNumeros(formParameters.get("clavesF"), ",");
			//logger.info(formParameters.get("clavesF")+"  "+formParameters.get("idUsuarioF"));
			AdministradorArchivosBusiness obj = (AdministradorArchivosBusiness) contexto.obtenerBean("administradorArchivosBusiness");
			ParametroLayoutDto dto = new ParametroLayoutDto();
			int op=0,z=0;
			int importados = 0;
			int total=0;
			String mensajes="";
			String mensajesUsuario="";
			obj.setLbReferenciaPorChequera(referenciaC);
			obj.setLbReferenciaNumerica(referenciaN);
			obj.setLiLongitudReferencia(longitud);
			obj.setIdUsuario(id);
			
			do{
				dto.setPsArchivos("");
				dto.setPlRegLeidos(0);
				dto.setPlRegSinChequera(0);
				dto.setPlRegImportados(0);
				dto.setPsChequerasInexistentes("");
				
				op = vec[z];
				
				switch(op){
					case ConstantesSet.BANAMEX:
						if(obj.leerBanamex(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexMismoDia(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexMismoDia(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexTEF(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if(obj.leerBanamexVersionInternet(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						
						if(obj.leerCitibankDlsProcedimiento(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANAMEX);
							else
								obj.actualizarFechaBanca("", obj.BANAMEX);
						}
						break;
					case ConstantesSet.BANCOMER:
						/*if(obj.leerBancomer(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
							else
								obj.actualizarFechaBanca("", obj.BANCOMER);
						}*/
						obj.leeEdosCtaAuto();
						
						if(obj.leeNetCashC43(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
							else
								obj.actualizarFechaBanca("", obj.BANCOMER);
						}
						importados += dto.getPlRegImportados();
					    total += importados;
						importados=0;
						if(obj.leerbancomerProcedimiento(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANCOMER);
							else
								obj.actualizarFechaBanca("", obj.BANCOMER);
						}
						break;
					case ConstantesSet.SANTANDER:
						if(obj.leerSantander(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
					    importados += dto.getPlRegImportados();
					    
					    dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
					    
						if(obj.leerSantanderBuzon(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
					    
						if(obj.leerSantanderTodasChequeras(dto)){
					    	if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.SANTANDER);
							else
								obj.actualizarFechaBanca("", obj.SANTANDER);
					    }
					    importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						break;
					case ConstantesSet.BITAL:
						/*if (obj.leerHSBCVersionOficial(dto)) {
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.HSBC);
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						}
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if (obj.leerHSBCTodasColumnas(dto))
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.HSBC);
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						if (importados > 0)
							obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
						importados += dto.getPlRegImportados();
						
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if (obj.leerHSBCTodasColumnasTodasChequeras(dto))
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						importados += dto.getPlRegImportados();
						
						if (importados > 0)
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
						total+=importados;
						importados=0;
						*/
						dto.setPsArchivos("");
						dto.setPlRegLeidos(0);
						dto.setPlRegSinChequera(0);
						dto.setPlRegImportados(0);
						dto.setPsChequerasInexistentes("");
						
						if (obj.leerHSBCGNP(dto))
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
							else
								obj.actualizarFechaBanca("", obj.HSBC);
						importados += dto.getPlRegImportados();
						
						if (importados > 0)
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarSaldoChequeraV2(obj.HSBC, dto.getPsArchivos());
						total+=importados;
						importados=0;
						break;
					case ConstantesSet.CITIBANK_DLS:
						//AdministradorArchivosBusiness adminbuss = new AdministradorArchivosBusiness();
						//adminbuss.leerCitibankDlsProcedimiento();
						//obj.leerCitibankDlsProcedimiento(dto);//lee el procedimeinto de lectura de sql avs
						//obj.leerCitibankDlsProcedimiento(dto);
							
						obj.leerCitibankDlsProcedimiento(dto);
						importados += dto.getPlRegImportados();
						total+=importados;
						importados=0;
						
						break;
					case ConstantesSet.BANORTE:
						if(obj.leeBanortePipe(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.BANORTE);
							else
								obj.actualizarFechaBanca("", obj.BANORTE);
						}
						importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						break;
					case ConstantesSet.INVERLAT:
						if(obj.leeInverlat(dto)){
							if (!dto.getPsArchivos().equals(""))
								obj.actualizarFechaBanca(dto.getPsArchivos(), obj.INVERLAT);
							else
								obj.actualizarFechaBanca("", obj.INVERLAT);
						}
						importados += dto.getPlRegImportados();
					    total+=importados;
						importados=0;
						break;
				}
				z++;
			}while(z<vec.length);
			
			for (int p = 0; p < obj.mensajes.size(); p++)
				if (obj.mensajes.get(p)!=null&&!obj.mensajes.get(p).equals(""))
					mensajes += obj.mensajes.get(p) + "\n";
			mensajes += "\nTotal Importados: " + total;
			if(obj.mensajesUsuario.size()>1){
				mensajesUsuario += "\n\nMesanjes de Usuario \n";
				for (int i = 0; i < obj.mensajesUsuario.size(); i++)
					mensajesUsuario += obj.mensajesUsuario.get(i) + "\n";
			}
			if(mensajes!=null || mensajesUsuario!=null || !mensajes.equals("") || !mensajesUsuario.equals("")){
				result.errors = new HashMap<String, String>();
				result.success=true;
				result.mensajes=mensajes;
				result.mensajesUsuario=mensajesUsuario;
				result.total=total;
			}
			}
		}
		catch(Exception e){
			result.success=false;
			e.printStackTrace();
			logger.error(e);
		}
		return result;
	
		
	}
}
