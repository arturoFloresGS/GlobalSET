package com.webset.set.global.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.global.dao.GlobalDao;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.ConfiguraSetDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.UsuarioLoginDto;

/**
 * Esta clase obtiene valores globales que se utilizar�n durante
 * los procesos de la aplicaci�n
 * @author Cristian Garcia Garcia
 * @since 02/Mayo/2011
 */
public class GlobalBusiness {
	private Bitacora bitacora = new Bitacora();
	private GlobalDao globalDao;
	private GlobalSingleton globalSingleton;
	private Funciones funciones = new Funciones();
	
	/**
	 * Este m�todo obtiene propiedades de los usuarios de la base
	 * de datos para cargarlos en el GlobalSingleton
	 * @param idUsuario
	 * @return retorna un mapa con los marametros paterno, paterno, materno, nombre
	 * no_empresa, nom_empresa, id_caja, desc_caja, no_cuenta_emp
	 */
//	public List<Map<String, Object>> obtenerPropiedadesUsuarios(){
//		List<Map<String,Object>> mapPropUsuario = new ArrayList<Map<String,Object>>();
//		try{
//			mapPropUsuario = globalDao.obtenerPropiedadesUsuarios();
//			globalSingleton = GlobalSingleton.getInstancia();
//			globalSingleton.setPropiedadesUsuario(mapPropUsuario);
//		}catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
//					+ "P:Global, C:GlobalBusiness, M:obtenerPropiedadesUsuario");
//		}
//		return mapPropUsuario;
//	}
	
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		return globalDao.consultarDatosComboGral(dto);
	}
	
	public Date obtenerFechaHoy(){
		Date dFechaHoy = null;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			dFechaHoy = globalDao.obtenerFechaHoy();
			globalSingleton.setFechaHoy(dFechaHoy);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:obtenerFechaHoy");
		}
		return dFechaHoy;
	}
	
	public List<LlenaComboGralDto> llenarComboEmpresasConcentradoras(int idUsuario, boolean bMantenimiento){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			list = globalDao.consultarEmpresasConcentradoras(idUsuario, bMantenimiento);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:llenarComboEmpresasConcentradoras");
		}
		return list;
	}
	 // PAEE
	public String graficaDatosBanco(int empresa){
		String  data = "";
		try{
			data = globalDao.consultarDatosGraficaBanco(empresa);
			
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:graficaDataBank");
		}
		data = data.replace('{',' ');
		data = data.replace('=',' ');
		data = data.replace('}',' ');
		
		return data;
	}
	
	//PAEE
	
	
	
	
	
	public List<LlenaComboDivisaDto> llenarComboDivisas(String condicion)
	{
		List<LlenaComboDivisaDto> listDivisas= new ArrayList<LlenaComboDivisaDto>();
		try{
			listDivisas = globalDao.consultarDivisas(condicion);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:llenarComboDivisas");
		}
		return listDivisas;
	}
	
	public void llenarListaConfiguraSet(){
		List<ConfiguraSetDto> listDatosConfiguraSet = new ArrayList<ConfiguraSetDto>();
		try{
			listDatosConfiguraSet = globalDao.consultarDatosConfiguraSet();
			globalSingleton = GlobalSingleton.getInstancia();
			globalSingleton.setListConfiguraSet(listDatosConfiguraSet);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:llenarDatosConfiguraSet");
		}
	}
	
	public boolean validarUsuarioAutenticado(int idUsr,String psw){
		boolean validar = false;
		try{
			validar = globalDao.validarUsuarioAutenticado(idUsr, funciones.encriptador(psw));
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Global, C:GlobalBusiness, M:validarUsuarioAutenticado");
		}
		return validar;
	}
	
	public boolean obtenerUsuariosActivos(){
		boolean bActivo = false;
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			bActivo = globalSingleton.obtenerUsuarioActivo();
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:obtenerUsuariosActivos");
		}
		return bActivo;
	}
	
	/**
	 * Este metodo agrega a los usuarios de session 
	 * a la lista del GlobalSingleton
	 * @param iIdUsuario
	 * @param sCodSession
	 */
	public void agregarUsuarioActivo(int iIdUsuario, String sCodSession){
		try{
			UsuarioLoginDto usuarioLoginDto = globalDao.obtenerPropiedadesUsuario(
					iIdUsuario, sCodSession);
			
			WebContextManager.get().getSession().setAttribute("usuarioLogin", usuarioLoginDto);
			WebContextManager.get().getSession().setAttribute("facultades", globalDao.getFacultades(usuarioLoginDto.getIdPerfil()));
			
			globalSingleton = GlobalSingleton.getInstancia();
			globalSingleton.setUsuarioLoginDto(
					usuarioLoginDto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
					+ "P:Seguridad, C:SegUsuarioBusiness, M:agregarUsuarioActivo");
		}
	}
	
	public void quitarUsuarioActivo(){
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			globalSingleton.borrarUsuarioActivo();			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:quitarUsuarioActivo");
		}
	}
	
	/**
	 * Este metodo obtiene la hora actual en formato
	 * @return
	 */
	public String obtenerHoraActualFormato12(){
		try{
			return funciones.obtenerHoraActual(true);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:obtenerHoraActualFormato12");
			return"";
		}
	}
	
	public List<EmpresaDto> llenarComboEmpresasCoinversoras(int idUsuario, int concentradora, String idDivisa){
		List<EmpresaDto> listEmp = new ArrayList<EmpresaDto>();
		try{
			listEmp = globalDao.consultarEmpresasCoinversoras(idUsuario, concentradora, idDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:llenarComboEmpresasCoinversoras");
		}
		return listEmp;
	}
	
	public String obtenerValorConfiguraSet(int iIndice){
		String sValor = "";
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			sValor = globalSingleton.obtenerValorConfiguraSet(iIndice);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:obtenerValorConfiguraSet");
		}
		return sValor;
	}
	
	/**
	 * Este m�todo obtiene las empresas asignadas a un usuario,
	 * retornando las mismas como cadena.
	 * @param iIdUsuario
	 * @return
	 */
	public String obtenerCadenaEmpresasUsuario(int iIdUsuario){
		String sEmpresas = "";
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try{
			listEmp = globalDao.consultarEmpresasUsuario(iIdUsuario);
			for(int i = 0 ; i < listEmp.size() ; i ++)
				sEmpresas += listEmp.get(i).getId() + ",";
			sEmpresas = sEmpresas.substring(0, sEmpresas.length() - 1);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Global, C:GlobalBusiness, M:obtenerCadenaEmpresasUsuario");
			return "";
		}
		return sEmpresas;
	}
	
	/**
	 * En este m�todo asigna el resultado de la consulta, 
	 * que contiene los perfiles de los usuarios a una lista
	 * en la clase GlobalSingleton
	 */
//	public void cargarPerfiles(){
//		try{
//			globalSingleton = GlobalSingleton.getInstancia();
//			globalSingleton.setPerfilesUsuario(globalDao.consultarPerfiles());
//		}catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
//					+ "P:Global, C:GlobalBusiness, M:cargarPerfiles");
//		}
//	}
	
	/**
	 * Este m�todo obtiene las empresas asignadas al usuario,
	 * el usuario lo tomamos del singleton que este activo en session
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerEmpresasUsuario(){
		List<LlenaComboGralDto> listEmp = new ArrayList<LlenaComboGralDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			listEmp = globalDao.consultarEmpresasUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalBusiness, M:obtenerEmpresasUsuario");
		}
		return listEmp;
	}

	/**
	 * Este m�todo obtiene las cajas asignadas a un usuario,
	 * es utilizado en la ventana de Cambio de Empresa
	 * 
	 * @return
	 */
	public List<LlenaComboGralDto> obtenerCajasUsuario(){
		List<LlenaComboGralDto> listCajas = new ArrayList<LlenaComboGralDto>();
		try{
			globalSingleton = GlobalSingleton.getInstancia();
			listCajas = globalDao.consultarCajasUsuario(globalSingleton.getUsuarioLoginDto().getIdUsuario());
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalBusiness, M:obtenerCajasUsuario");
		}
		return listCajas;
	}
	
	public Map<String, Object> cambiarEmpresaCajaUsuario(int iIdEmpresa, String sNomEmpresa, int iIdCaja, String sDescCaja)
	{
		Map<String, Object> mapProp = new HashMap<String, Object>();
		try
		{
			globalSingleton = GlobalSingleton.getInstancia();
			mapProp = globalSingleton.cambiarPropiedadUsuario(iIdEmpresa, sNomEmpresa, iIdCaja, sDescCaja);
		}
		catch(Exception e)
		{
			bitacora.insertarRegistro(new Date().toString()+ " "+ e.toString()
					+ "P:Global, C:GlobalBusiness, M:cambiarEmpresaCajaUsuario");
		}
		return mapProp;
	}
	
	public GlobalDao getGlobalDao() {
		return globalDao;
	}

	public void setGlobalDao(GlobalDao globalDao) {
		this.globalDao = globalDao;
	}
}
