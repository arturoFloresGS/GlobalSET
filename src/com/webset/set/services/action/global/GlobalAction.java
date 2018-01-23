package com.webset.set.services.action.global;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.global.business.GlobalBusiness;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/**
 * Esta clase obtiene valores globales que se utilizar�n durante
 * los procesos de la aplicaci�n
 * @author Cristian Garcia Garcia
 * @since 02/Mayo/2011
 */
public class GlobalAction {
	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new  Contexto();
	private GlobalSingleton globalSingleton;
	Funciones funciones = new Funciones();
	
	/**
	 * Este m�todo obtiene propiedades de los usuarios de la base
	 * de datos para cargarlos en el GlobalSingleton
	 * @param idUsuario
	 * @return retorna un mapa con los marametros paterno, paterno, materno, nombre
	 * no_empresa, nom_empresa, id_caja, desc_caja, no_cuenta_emp
	 */
//	@DirectMethod
//	public void obtenerPropiedadesUsuarios(){
//		try{
//			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
//			globalBusiness.obtenerPropiedadesUsuarios();
//		}catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
//					+ "P:Global, C:GlobalAction, M:obtenerPropiedadesUsuario");
//		}
//	}
	
	@DirectMethod
	public Map<String,Object> obtenerSingleton(){
		Map<String,Object> listPropUsuarios = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			globalSingleton = GlobalSingleton.getInstancia();
			listPropUsuarios = globalSingleton.obtenerPropiedadesUsuario();
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:obtenerSingleton");
		}
		return listPropUsuarios;
	}
	
	@DirectMethod
	public boolean obtenerBanderaDatosCargados(){
		boolean bPropiedadesCargadas = false;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			globalSingleton = GlobalSingleton.getInstancia();
			bPropiedadesCargadas = globalSingleton.isBPropiedadesCargadas();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:obtenerBanderaDatosCargados");
		}
		return bPropiedadesCargadas;
	}
	
	/**
	 * llenado de combo general
	 * @param campoUno
	 * @param campoDos
	 * @param tabla
	 * @param condicion
	 * @param orden
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboGeneral(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list=globalBusiness.llenarComboGral(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Global, C:GlobalAction, M:llenarComboGeneral");	
		}
		return list;
	}
	
	@DirectMethod
	public String obtenerFechaHoy(){
		String sFechaHoy = null;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			sFechaHoy = funciones.ponerFechaSola(globalBusiness.obtenerFechaHoy());
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:obtenerFechaHoy");
		}
		return sFechaHoy;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboEmpresasConcentradoras(boolean bMantenimiento){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			list = globalBusiness.llenarComboEmpresasConcentradoras(globalSingleton.getUsuarioLoginDto().getIdUsuario(),
					bMantenimiento);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:llenarComboEmpresasConcentradoras");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboDivisaDto> llenarComboDivisas(String condicion)
	{
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			listDivisas = globalBusiness.llenarComboDivisas(condicion);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:llenarComboDivisas");
		}
		return listDivisas;
	}
	
	@DirectMethod
	public boolean validarUsuarioAutenticado(int idUsr,String psw){
		boolean validar = false;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			validar = globalBusiness.validarUsuarioAutenticado(idUsr, psw);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:validarUsuarioAutenticado");
		}
		return validar;
	}
	
	
	@DirectMethod
	public void llenarDatosConfiguraSet(){
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			globalBusiness.llenarListaConfiguraSet();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:llenarDatosConfiguraSet");
		}
	}
	
	@DirectMethod
	public boolean obtenerUsuariosActivos(){
		boolean bActivo = false;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			bActivo = globalBusiness.obtenerUsuariosActivos();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:obtenerUsuariosActivos");
		}
		return bActivo;
	}
	
	@DirectMethod
	public void agregarUsuarioActivo(int iIdUsuario, String sCodSession){
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			globalBusiness.agregarUsuarioActivo(iIdUsuario, sCodSession);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Seguridad, C:SegUsuarioBusiness, M:agregarUsuarioActivo");
		}
	}
	
	@DirectMethod
	public void quitarUsuarioActivo(){
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			globalBusiness.quitarUsuarioActivo();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:quitarUsuarioActivo");
		}
	}
	
	@DirectMethod 
	public String obtenerHoraActualFormato12(){
		try{
			
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			return globalBusiness.obtenerHoraActualFormato12();
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:quitarUsuarioActivo");
			return "";
		}
	}
	
	@DirectMethod
	public List<EmpresaDto> llenarComboEmpresasCoinversoras(int concentradora, String idDivisa){
		List<EmpresaDto> listEmp = new ArrayList<EmpresaDto>();
		globalSingleton = GlobalSingleton.getInstancia();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			int idUsuario = globalSingleton.getUsuarioLoginDto().getIdUsuario();
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			listEmp = globalBusiness.llenarComboEmpresasCoinversoras(idUsuario, concentradora, idDivisa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:llenarComboEmpresasConcentradoras");
		}
		return listEmp;
	}
	
		
	/**
	 * Este m�todo obtiene el valor del configura_set,
	 * normalmente utilizado para hacer llamadas desde cualquier js
	 * @param iIndice
	 * @return
	 */
	@DirectMethod
	public String obtenerValorConfiguraSet(int iIndice){
		String sValor = "";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			sValor = globalBusiness.obtenerValorConfiguraSet(iIndice);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:obtenerValorConfiguraSet");
		}
		return sValor;
	}
	
//	@DirectMethod
//	public void cargarPerfiles(){
//		try{
//			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
//			globalBusiness.cargarPerfiles();
//		}catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
//					+ "P:Global, C:GlobalAction, M:cargarPerfiles");
//		}
//	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboEmpresasUsuario(){
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			listEmp = globalBusiness.obtenerEmpresasUsuario(); 
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:llenarComboEmpresasUsuario");
		}
		return listEmp;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboCajasUsuario(){
		List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			listCajas = globalBusiness.obtenerCajasUsuario(); 
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalAction, M:llenarComboCajasUsuario");
		}
		return listCajas;
	}
	
	@DirectMethod
	public Map<String, Object> cambiarEmpresaCajaUsuario(int iIdEmpresa, String sNomEmpresa, int iIdCaja, String sDescCaja)
	{
		Map<String, Object> mapProp = new HashMap<String, Object>();
		try
		{
			if (Utilerias.haveSession(WebContextManager.get())) {
			GlobalBusiness globalBusiness = (GlobalBusiness) contexto.obtenerBean("globalBusiness");
			mapProp = globalBusiness.cambiarEmpresaCajaUsuario(iIdEmpresa, sNomEmpresa, iIdCaja, sDescCaja);
			}
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalAction, M:cambiarEmpresaCajaUsuario");
		}
		return mapProp;
	}
	
}

