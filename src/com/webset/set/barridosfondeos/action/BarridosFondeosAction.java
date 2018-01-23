
/**
 * @autor COINSIDE
 */

package com.webset.set.barridosfondeos.action;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRDataSource;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
//import com.softwarementors.extjs.djn.servlet.ssm.WebContext;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.barridosfondeos.dto.BusquedaFondeoDto;
import com.webset.set.barridosfondeos.dto.FilialDto;
import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;
//import com.webset.set.barridosfondeos.integration.BarridosFondeosMail;
import com.webset.set.barridosfondeos.service.BarridosFondeosService;
import com.webset.set.coinversion.dto.ParamBusquedaFondeoDto;
import com.webset.set.coinversion.service.CoinversionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class BarridosFondeosAction {
	
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
	//List<Boolean> resultPerfil;// = new ArrayList<Boolean>();
	String sLetras = " ";
	int iCen = 0;
	int iDes = 0;
	int iUni = 0;
	
	
	/* @ DirectMethod
	public String consultarConfiguraSet(int indice){
		String valor="";
		try{
			if (!Utilerias.haveSession(WebContextManager.get())&& !Utilerias.tienePermiso(WebContextManager.get(),50))
				return null;
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
			valor = barridosFondeosService.consultarConfiguraSet(indice);
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:consultarConfiguraSet");			
		}
		return valor;
	}*/
	
	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresasConcent(int idUsuario, boolean mantenimiento)
	{
		List<LlenaComboEmpresasDto>lista=new ArrayList<LlenaComboEmpresasDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				lista = barridosFondeosService.obtenerEmpresas(idUsuario, mantenimiento);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerEmpresas");			
		}
		return lista;
	}
	
	@DirectMethod
	public List<FilialDto> obtenerFiliales (String noEmpresa, int idUsuario)
	{
		List<FilialDto> lista = new ArrayList<FilialDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				lista = barridosFondeosService.obtenerEmpresasFiliales(Integer.parseInt(noEmpresa), idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerEmpresas");			
		}
		return lista;
	}
	
	/**
	 * Metodo para obtener la empresa raiz del arbol en la pantalla de 
	 * barridos y fondeos automaticos.
	 * @param vbExistentes
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> obtenerRaizArbolFA (boolean bExistentes, String tipoOperacion)
	{
		List<LlenaComboGralDto> lista = new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				lista = barridosFondeosService.obtenerTipoArbol(bExistentes, tipoOperacion);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerEmpresas");			
		}
		return lista;
	}
	
	/**
	 * Metodo para obtener la empresa raiz del arbol en la pantalla de 
	 * barridos y fondeos automaticos.
	 * @param vbExistentes
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto> obtenerEmpresasArbolFondeo (int noEmpresaRaiz)
	{
		List<LlenaComboGralDto> lista = new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				lista = barridosFondeosService.obtenerEmpresasArbolFondeo(noEmpresaRaiz);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosAction, M:obtenerEmpresasArbolFondeo");			
		}
		return lista;
	}
	
	/**
	 * Metodo para obtener la configuracion de consulta (igual o diferente) de un arbol
	 * @param tipoArbol
	 * @return
	 */
	@DirectMethod
	public String obtenerIgualDiferente (int tipoArbol)
	{
		String resultado = new String();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				resultado = barridosFondeosService.obtenerIgualDiferente(tipoArbol);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosAction, M:obtenerIgualDiferente");			
		}
		return resultado;
	}
	
	
	
	/*--- config de arboles ---*/
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbArbol(boolean bExistentes){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpRaiz;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			listEmpRaiz = barridosFondeosService.consultarArboles(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbArbol");
		}
		return listEmpRaiz;
	} 

	
	

	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbArbolHijos(boolean bExistentes, int idRaiz){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpRaiz;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			listEmpRaiz = barridosFondeosService.consultarArbolesHijos(bExistentes, idRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbArbolHijos");
		}
		return listEmpRaiz;
	} 

	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbArbolHijosInterempresas(boolean bExistentes, int idRaiz){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpRaiz;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			listEmpRaiz = barridosFondeosService.consultarArbolesHijosInterempresas(bExistentes, idRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbArbolHijosInterempresas");
		}
		return listEmpRaiz;
	} 

	
	
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbArbolInterempresas(boolean bExistentes){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpRaiz;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			listEmpRaiz = barridosFondeosService.consultarArbolesInterempresas(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbArbolInterempresas");
		}
		return listEmpRaiz;
	}

	
	

	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresaRaiz(boolean bExistentes, int idArbol){
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpRaiz;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			listEmpRaiz = barridosFondeosService.obtenerEmpresasRaiz(bExistentes,idArbol);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbEmpresaRaiz");
		}
		return listEmpRaiz;
	}
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresaRaizInterempresas(boolean bExistentes){
		
		List<LlenaComboGralDto> listEmpRaiz = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpRaiz;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			listEmpRaiz = barridosFondeosService.obtenerEmpresasRaizInterempresas(bExistentes);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbEmpresaRaizInterempresas");
		}
		return listEmpRaiz;
		
		
		
	}
	
	
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresasHijo(int iEmpRaiz){
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpHijo;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			
			listEmpHijo = barridosFondeosService.obtenerEmpresasHijo(iEmpRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbEmpresasHijo");
		}return listEmpHijo;
	}
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbEmpresasHijoIn(int iEmpRaiz){
		System.out.println("Angel llega esto de NS.StoreEmpresasHijo\n"+iEmpRaiz);
		List<LlenaComboGralDto> listEmpHijo = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return listEmpHijo;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			
			listEmpHijo = barridosFondeosService.obtenerEmpresasHijoIn(iEmpRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbEmpresasHijoIn");
		}return listEmpHijo;
	}
	
	
	
	@DirectMethod 
	public List<LlenaComboGralDto> llenarCmbTipoOperacion(){
		List<LlenaComboGralDto> operaciones = new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()))
			return operaciones;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			
			operaciones = barridosFondeosService.consultarTipoOperacion();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: llenarCmbTipoOperacion");
		}return operaciones;
	}

	@DirectMethod 
	public String obtenerArbolEmpresa(int iEmpresaRaiz){
		String struc = "";
//		if(!Utilerias.haveSession(WebContextManager.get()))
//			return struc;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			struc = barridosFondeosService.obtenerArbolEmpresa(iEmpresaRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: obtenerArbolEmpresa");
		}return struc;
	}

	
	@DirectMethod 
	public String obtenerArbolEmpresaIn(int iEmpresaRaiz){
		String struc = "";
//		if(!Utilerias.haveSession(WebContextManager.get()))
//			return struc;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			struc = barridosFondeosService.obtenerArbolEmpresaInterempresas(iEmpresaRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: obtenerArbolEmpresaInterempresas");
		}return struc;
	}
	
	
	@DirectMethod 
	public String obtenerArbolEmpresaInterempresas(int iEmpresaRaiz){
		String struc = "";
//		if(!Utilerias.haveSession(WebContextManager.get()))
//			return struc;
		try{																										   
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			struc = barridosFondeosService.obtenerArbolEmpresaInterempresas(iEmpresaRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: obtenerArbolEmpresaInteresmpresas");
		}return struc;
	}
	
	
	
	
	@DirectMethod
	public JRDataSource obtenerReporteArbolEmpresa(ServletContext context){
		JRDataSource jrDataSource = null;
		try{
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			jrDataSource = barridosFondeosService.obtenerReporteArbolEmpresa();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: obtenerReporteArbolEmpresa");
		}return jrDataSource;
	}
	
	@DirectMethod
	public String agregarNodosArbol(boolean bNvoHijo,
									String sRuta,
									int iIdEmpresaRaiz, 
									int iIdEmpresa, 
									double uMonto,
									String nombreArbol,
									String tipoValor, 
									int tipoOperacion,
									int iIdEmpresaPadre){
		
		System.out.println(tipoValor+" llego esta madre");
		String sMsgUsuario = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			sMsgUsuario = barridosFondeosService.agregarNodosArbol(bNvoHijo, sRuta, iIdEmpresaRaiz, 
					iIdEmpresa, uMonto, nombreArbol, tipoValor, tipoOperacion, iIdEmpresaPadre); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: agregarNodosArbol");
		}return sMsgUsuario;
	}

	
	//angel aqui metodo del boton NuevoArbol -> ejecutar y nuevo nodo


	@DirectMethod
	public String agregarNodosArbolIn(boolean bNvoHijo,//que si es nuevo hijo si dice false es padre
									String sRuta,//trae la clave de donde viene de la empresa raiz ala que se le metera el hijo
									int iIdEmpresaRaiz,//trae la clave de la empresa raiz ala que se le metera el hijo 
									int iIdEmpresaHijo,//Id de la empresa 
									double uMonto,//monto en flotante
									String nombreArbol,//trae un dato en blanco ''
									//String tipoValor,//trae el dato del combo tipodevalor 
									int tipoOperacion,//treae el numero de la operacion
									int iIdEmpresaPadre){//trae el Id del padre ala que se insertara

		
		

		String sMsgUsuario = "en nodos hojso angel";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			sMsgUsuario = barridosFondeosService.agregarNodosArbolIn(bNvoHijo, sRuta, iIdEmpresaRaiz, 
					iIdEmpresaHijo, uMonto, nombreArbol, tipoOperacion, iIdEmpresaPadre); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: agregarNodosArbolIn");
		}return sMsgUsuario;
	}
	
	
	@DirectMethod
	public String eliminarNodosArbol(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre){
		String sMsgUsuario = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			sMsgUsuario = barridosFondeosService.eliminarNodosArbol(iIdEmpresaRaiz, iIdEmpresaActual, iIdEmpresaPadre);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: eliminarNodosArbol");
		}return sMsgUsuario;
	}
	

	
	
	
	@DirectMethod
	public String actualizarMonto(int monto, int idEmpresa, int idRaiz){	
		System.out.println("llegan estos datos\n");
		System.out.println("monto\n"+ monto);
		System.out.println("empresa\n"+idEmpresa);
		System.out.println("raiz\n"+idRaiz);

		String sMsgUsuario = "en nodos hojso angel";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			sMsgUsuario = barridosFondeosService.actualizarMonto(monto, idEmpresa, idRaiz ); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: agregarNodosArbolIn");
		}return sMsgUsuario;
	}
	
	
	
	
	
	
	
	
	
	@DirectMethod
	public String actualizarMontoa(int monto, int idEmpresa, int idRaiz){
		String sMsgUsuario = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			sMsgUsuario = barridosFondeosService.actualizarMonto( monto,  idEmpresa, idRaiz);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: actualizarMonto");
		}return sMsgUsuario;
	}
	
	
	
	
	
	@DirectMethod
	public String eliminarNodosArbolIn(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre){
		String sMsgUsuario = "";
		if(!Utilerias.haveSession(WebContextManager.get()))
			return sMsgUsuario;
		try{
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService) contexto.obtenerBean("barridosFondeosBusinessImpl");
			sMsgUsuario = barridosFondeosService.eliminarNodosArbolIn(iIdEmpresaRaiz, iIdEmpresaActual, iIdEmpresaPadre);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P: BarridosFondeos C: BarridosFondeosAction M: eliminarNodosArbolIn");
		}return sMsgUsuario;
	}
	
	
	/*--- termina config arboles ---*/
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancosRaiz(int idEmpresa, String idDivisa)
	{
		List<LlenaComboGralDto>lista=new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				lista = barridosFondeosService.consultarBancosRaiz(idEmpresa, idDivisa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerEmpresas");			
		}
		return lista;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerChequerasRaiz(int idEmpresa, int idBanco, String idDivisa)
	{
		List<LlenaComboGralDto>lista=new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				lista = barridosFondeosService.consultarChequerasRaiz(idEmpresa, idBanco, idDivisa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:BarridosFondeos, C:BarridosFondeosAction, M:obtenerChequerasRaiz");			
		}
		return lista;
	}
	
	@DirectMethod
	public List<FondeoAutomaticoDto> consultarFondeoAutomatico(int idEmpresa, int idEmpresaRaiz, int idBanco, 
			String idDivisa, String idChequera, boolean mismoBanco, String tipoBusqueda, int idUsuario)
	{
		BusquedaFondeoDto dtoBus = new BusquedaFondeoDto();
		List<FondeoAutomaticoDto>lista=new ArrayList<FondeoAutomaticoDto>();
		try{
			System.out.println("entro a consulta action foneoAutomatico");
		//	if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
				dtoBus.setIdEmpresaRaiz(idEmpresaRaiz >0 ? idEmpresaRaiz : 0);
				dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
				dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
				dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
				dtoBus.setChkMismoBanco(mismoBanco);
				dtoBus.setSTipoBusqueda(tipoBusqueda != null && !tipoBusqueda.equals("") ? tipoBusqueda : "");
				dtoBus.setIdUsuario(idUsuario);
				
				BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
				lista = barridosFondeosService.consultarFondeoAutomatico(dtoBus);
//			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerEmpresas");			
		}
		return lista;
	}
	@DirectMethod
	public List<LlenaComboGralDto> consultarChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco){
		List<LlenaComboGralDto> listDatos = new ArrayList<LlenaComboGralDto>();
		try{
			CoinversionService barridosFondeosService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
			listDatos = barridosFondeosService.llenarComboChequeraFondeo(iIdEmpresa, sIdDivisa, iIdBanco);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionAction, M:llenarComboEmpresaRaiz");
			return null;
		}
		return listDatos;
	}
	@DirectMethod
	public String ejecutarFondeoAutomatico(String datosGrid, int idEmpresa, int idEmpresaRaiz, int idBanco, 
			String idDivisa, String idChequera, boolean chkMismoBanco, String sTipoBusqueda, boolean bVisibleMontoMinFondeo,
			String montoMinFondeo, int idUsuario, String nomEmpresaRaiz){
//		if (!(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50))) 
//			return null;
		Gson gson = new Gson();
		//Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		String salida = "";
		
		List<FondeoAutomaticoDto> listGridFondeo = new ArrayList<FondeoAutomaticoDto>();
		try{
			System.out.println("entro a ejecutarFondeoAutomatico action");
			
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
			List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++)
			{
				FondeoAutomaticoDto dto = new FondeoAutomaticoDto();
					dto.setOrden(funciones.convertirCadenaInteger(objParams.get(i).get("orden")));
					dto.setPrestamos(funciones.convertirCadenaDouble(objParams.get(i).get("prestamos")));
			        dto.setNoEmpresaOrigen(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaOrigen")));
			     	dto.setNomEmpresaOrigen(funciones.validarCadena(objParams.get(i).get("nomEmpresaOrigen").toString()));
			     	dto.setNoEmpresaDestino(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaDestino")));
			     	dto.setNomEmpresaDestino(funciones.validarCadena(objParams.get(i).get("nomEmpresaDestino").toString()));
			     	dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
			     	dto.setIdBanco(funciones.convertirCadenaInteger(objParams.get(i).get("idBanco")));
			     	dto.setIdChequera(funciones.validarCadena( objParams.get(i).get("idChequera").toString()));
			    	dto.setIdCaja(Integer.parseInt(objParams.get(i).get("idCajaE")));
			    	
			     	if (objParams.get(i).get("idChequeraPadre") != null)
			     		dto.setIdChequeraPadre(funciones.validarCadena( objParams.get(i).get("idChequeraPadre").toString()));
			     	else
			     		dto.setIdChequeraPadre("");
			     	dto.setSaldoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoChequera")));
			     	dto.setSaldoMinimoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoMinimoChequera")));
			     	dto.setImporteF(funciones.convertirCadenaDouble(objParams.get(i).get("importeF")));
			     	dto.setTipoCambio(funciones.convertirCadenaDouble(objParams.get(i).get("tipoCambio")));
			     	dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa")));
			     	dto.setPm(funciones.convertirCadenaDouble(objParams.get(i).get("pm")));
			     	dto.setPmCheques(funciones.convertirCadenaDouble(objParams.get(i).get("pmCheques")));
			     	dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
			     	dto.setImporteTraspaso(funciones.convertirCadenaDouble(objParams.get(i).get("importeTraspaso")));
			     	dto.setSaldoCoinversion(funciones.convertirCadenaDouble(objParams.get(i).get("saldoCoinversion")));
		     	listGridFondeo.add(dto);
			}
			
			BusquedaFondeoDto dtoBus = new BusquedaFondeoDto();
			dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
			dtoBus.setIdEmpresaRaiz(idEmpresaRaiz >0 ? idEmpresaRaiz : 0);
			dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
			dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
			dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
			dtoBus.setChkMismoBanco(chkMismoBanco);
			dtoBus.setSTipoBusqueda(sTipoBusqueda != null && !sTipoBusqueda.equals("") ? sTipoBusqueda : "");
			dtoBus.setIdUsuario(idUsuario);
			dtoBus.setNomEmpresaRaiz(nomEmpresaRaiz);
			dtoBus.setTipoSaldo(funciones.convertirCadenaInteger( objParams.get(0).get("idSaldo")));
			salida = barridosFondeosService.ejecutarFondeoAutomatico(listGridFondeo, dtoBus);
			/*
			BarridosFondeosMail barridosMail = new BarridosFondeosMail();
			WebContext context = WebContextManager.get();
			ServletContext contextEnviar = context.getServletContext();
			
			barridosMail.enviarFondeoAutomatico(datosGrid, idEmpresaRaiz, idUsuario, 'F', nomEmpresaRaiz, contextEnviar);
			*/
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarFondeoAutomatico");
		}
		return salida;
	}
	@DirectMethod
	public String prepararFondeoAutomatico(String datosGrid, int idEmpresa, int idEmpresaRaiz, int idBanco, 
			String idDivisa, String idChequera, boolean chkMismoBanco, String sTipoBusqueda, boolean bVisibleMontoMinFondeo,
			String montoMinFondeo, int idUsuario, String nomEmpresaRaiz){
		if (!(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50))) 
			return null;
		Gson gson = new Gson();
		//Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		String salida = "";
		
		List<FondeoAutomaticoDto> listGridFondeo = new ArrayList<FondeoAutomaticoDto>();
		try{
			
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
			List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++)
			{
				FondeoAutomaticoDto dto = new FondeoAutomaticoDto();
					dto.setOrden(funciones.convertirCadenaInteger(objParams.get(i).get("orden")));
					dto.setPrestamos(funciones.convertirCadenaDouble(objParams.get(i).get("prestamos")));
			        dto.setNoEmpresaOrigen(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaOrigen")));
			     	dto.setNomEmpresaOrigen(funciones.validarCadena(objParams.get(i).get("nomEmpresaOrigen").toString()));
			     	dto.setNoEmpresaDestino(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaDestino")));
			     	dto.setNomEmpresaDestino(funciones.validarCadena(objParams.get(i).get("nomEmpresaDestino").toString()));
			     	dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
			     	dto.setIdBanco(funciones.convertirCadenaInteger(objParams.get(i).get("idBanco")));
			     	dto.setIdChequera(funciones.validarCadena( objParams.get(i).get("idChequera").toString()));
			     
			     	if (objParams.get(i).get("idChequeraPadre") != null)
			     		dto.setIdChequeraPadre(funciones.validarCadena( objParams.get(i).get("idChequeraPadre").toString()));
			     	else
			     		dto.setIdChequeraPadre("");
			     	dto.setSaldoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoChequera")));
			     	dto.setSaldoMinimoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoMinimoChequera")));
			     	dto.setImporteF(funciones.convertirCadenaDouble(objParams.get(i).get("importeF")));
			     	dto.setTipoCambio(funciones.convertirCadenaDouble(objParams.get(i).get("tipoCambio")));
			     	dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa")));
			     	dto.setPm(funciones.convertirCadenaDouble(objParams.get(i).get("pm")));
			     	dto.setPmCheques(funciones.convertirCadenaDouble(objParams.get(i).get("pmCheques")));
			     	dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
			     	dto.setImporteTraspaso(funciones.convertirCadenaDouble(objParams.get(i).get("importeTraspaso")));
			     	dto.setSaldoCoinversion(funciones.convertirCadenaDouble(objParams.get(i).get("saldoCoinversion")));
			     	
		     	listGridFondeo.add(dto);
			}
			
			BusquedaFondeoDto dtoBus = new BusquedaFondeoDto();
			dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
			dtoBus.setIdEmpresaRaiz(idEmpresaRaiz >0 ? idEmpresaRaiz : 0);
			dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
			dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
			dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
			dtoBus.setChkMismoBanco(chkMismoBanco);
			dtoBus.setSTipoBusqueda(sTipoBusqueda != null && !sTipoBusqueda.equals("") ? sTipoBusqueda : "");
			dtoBus.setIdUsuario(idUsuario);
			dtoBus.setNomEmpresaRaiz(nomEmpresaRaiz);
			
			salida = barridosFondeosService.prepararFondeoAutomatico(listGridFondeo, dtoBus);
			/*
			BarridosFondeosMail barridosMail = new BarridosFondeosMail();
			WebContext context = WebContextManager.get();
			ServletContext contextEnviar = context.getServletContext();
			
			barridosMail.enviarFondeoAutomatico(datosGrid, idEmpresaRaiz, idUsuario, 'F', nomEmpresaRaiz, contextEnviar);
			*/
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarFondeoAutomatico");
		}
		return salida;
	}
	@DirectMethod
	public String ejecutarBarridoAutomatico(String datosGrid, int idEmpresa, int idEmpresaRaiz, int idBanco, 
			String idDivisa, String idChequera, boolean chkMismoBanco, String sTipoBusqueda, boolean bVisibleMontoMinFondeo,
			String montoMinFondeo, int idUsuario, String nomEmpresaRaiz){
		if (!(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50))) 
			return null;
		Gson gson = new Gson();
		//Map<String, Object> mapRetFondeo = new HashMap<String, Object>();
		String salida = "";
		
		List<FondeoAutomaticoDto> listGridFondeo = new ArrayList<FondeoAutomaticoDto>();
		try{
			
			BarridosFondeosService barridosFondeosService = (BarridosFondeosService)contexto.obtenerBean("barridosFondeosBusinessImpl");
			List<Map<String, String>> objParams = gson.fromJson(datosGrid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < objParams.size(); i++)
			{
				FondeoAutomaticoDto dto = new FondeoAutomaticoDto();
					dto.setOrden(funciones.convertirCadenaInteger(objParams.get(i).get("orden")));
					dto.setPrestamos(funciones.convertirCadenaDouble(objParams.get(i).get("prestamos")));
			        dto.setNoEmpresaOrigen(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaOrigen")));
			     	dto.setNomEmpresaOrigen(funciones.validarCadena(objParams.get(i).get("nomEmpresaOrigen").toString()));
			     	dto.setNoEmpresaDestino(funciones.convertirCadenaInteger(objParams.get(i).get("noEmpresaDestino")));
			     	dto.setNomEmpresaDestino(funciones.validarCadena(objParams.get(i).get("nomEmpresaDestino").toString()));
			     	dto.setDescBanco(funciones.validarCadena(objParams.get(i).get("descBanco").toString()));
			     	dto.setIdBanco(funciones.convertirCadenaInteger(objParams.get(i).get("idBanco")));
			     	dto.setIdChequera(funciones.validarCadena( objParams.get(i).get("idChequera").toString()));
			     	if (objParams.get(i).get("idChequeraPadre") != null)
			     		dto.setIdChequeraPadre(funciones.validarCadena( objParams.get(i).get("idChequeraPadre").toString()));
			     	else
			     		dto.setIdChequeraPadre("");
			     	dto.setSaldoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoChequera")));
			     	dto.setSaldoMinimoChequera(funciones.convertirCadenaDouble(objParams.get(i).get("saldoMinimoChequera")));
			     	dto.setImporteF(funciones.convertirCadenaDouble(objParams.get(i).get("importeF")));
			     	dto.setTipoCambio(funciones.convertirCadenaDouble(objParams.get(i).get("tipoCambio")));
			     	dto.setIdDivisa(funciones.validarCadena(objParams.get(i).get("idDivisa")));
			     	dto.setPm(funciones.convertirCadenaDouble(objParams.get(i).get("pm")));
			     	dto.setPmCheques(funciones.convertirCadenaDouble(objParams.get(i).get("pmCheques")));
			     	dto.setConcepto(funciones.validarCadena(objParams.get(i).get("concepto")));
			     	dto.setImporteTraspaso(funciones.convertirCadenaDouble(objParams.get(i).get("importeTraspaso")));
			     	dto.setImporteBarrido(funciones.convertirCadenaDouble(objParams.get(i).get("importeBarrido")));
			     	dto.setSaldoCoinversion(funciones.convertirCadenaDouble(objParams.get(i).get("saldoCoinversion")));
		     	listGridFondeo.add(dto);
			}
			
			BusquedaFondeoDto dtoBus = new BusquedaFondeoDto();
			dtoBus.setIdEmpresa(idEmpresa > 0 ? idEmpresa : 0);
			dtoBus.setIdEmpresaRaiz(idEmpresaRaiz >0 ? idEmpresaRaiz : 0);
			dtoBus.setIdBanco(idBanco > 0 ? idBanco : 0);
			dtoBus.setIdDivisa(idDivisa != null && !idDivisa.equals("") ? idDivisa : "");
			dtoBus.setIdChequera(idChequera != null && !idChequera.equals("") ? idChequera : "");
			dtoBus.setChkMismoBanco(chkMismoBanco);
			dtoBus.setSTipoBusqueda(sTipoBusqueda != null && !sTipoBusqueda.equals("") ? sTipoBusqueda : "");
			dtoBus.setIdUsuario(idUsuario);
			dtoBus.setNomEmpresaRaiz(nomEmpresaRaiz);
			
			salida = barridosFondeosService.ejecutarBarridoAutomatico(listGridFondeo, dtoBus);
			/*
			BarridosFondeosMail barridosMail = new BarridosFondeosMail();
			WebContext context = WebContextManager.get();
			ServletContext contextEnviar = context.getServletContext();
			
			barridosMail.enviarFondeoAutomatico(datosGrid, idEmpresaRaiz, idUsuario, 'B', nomEmpresaRaiz, contextEnviar);
			*/
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionAction, M:ejecutarFondeoAutomatico");
		}
		return salida;
	}
	
	/**
	 * Este metodo consulta los pagos pendientes
	 * @param iIdEmpresa
	 * @param iIdBanco
	 * @param sIdChequera
	 * @param iIdEmpRaiz
	 * @param sTipoBusqueda
	 * @param sIdDivisa
	 * @param idUsuario
	 * @return
	 */
	@DirectMethod
	public List<MovimientoDto> obtenerPagos(int iIdEmpresa, int iIdEmpRaiz, int iIdBanco, 
			String sIdDivisa, String sIdChequera, String sTipoBusqueda, int idUsuario)
	{
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<MovimientoDto> listConsPag = new ArrayList<MovimientoDto>(); 
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			ParamBusquedaFondeoDto dtoBus = new ParamBusquedaFondeoDto();
			CoinversionService coinversionService = (CoinversionService) contexto.obtenerBean("coinversionBusinessImpl");
			dtoBus.setIdEmpresa(funciones.validarEntero(iIdEmpresa));
			dtoBus.setIdBanco(funciones.validarEntero(iIdBanco));
			dtoBus.setIdChequera(funciones.validarCadena(sIdChequera));
			dtoBus.setIdEmpresaRaiz(funciones.validarEntero(iIdEmpRaiz));
			dtoBus.setSTipoBusqueda(funciones.validarCadena(sTipoBusqueda));
			dtoBus.setIdDivisa(funciones.validarCadena(sIdDivisa));
			dtoBus.setIdUsuario(idUsuario);
			
			listConsPag = coinversionService.obtenerPagos(dtoBus);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Coinversion, C:CoinversionAction, M:obtenerPagos");
		}
		return listConsPag;
	}
	
}
