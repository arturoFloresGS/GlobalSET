package com.webset.set.utilerias.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.service.CodigosService;
import com.webset.set.utilerias.service.GrupoEmpresasService;
import com.webset.set.utileriasmod.dto.CodigosDto;
import com.webset.utils.tools.Utilerias;

public class CodigosAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	CodigosService objCodigosService;
	
	@DirectMethod
	public List<CodigosDto> llenaComboEmpresas(){
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				listaResultado = objCodigosService.llenaComboEmpresas();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: llenaComboEmpresas");
		}return listaResultado;
	}
	
	@DirectMethod
	public HSSFWorkbook reporteGrupos(ServletContext context){
		HSSFWorkbook wb=null;
		try {
			
			objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl",context);
			wb=objCodigosService.reporteGrupos();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: reporteGrupos");
		}return wb;
	}
	
	@DirectMethod
	public HSSFWorkbook reporteGrupoPolizas(ServletContext context){
		HSSFWorkbook wb=null;
		try {
			
			objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl",context);
			wb=objCodigosService.reporteGrupoPolizas();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: reporteGrupoPolizas");
		}return wb;
	}
	
	@DirectMethod
	public HSSFWorkbook reporteGrupoRubros(ServletContext context){
		HSSFWorkbook wb=null;
		try {
			
			objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl",context);
			wb=objCodigosService.reporteGrupoRubros();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: reporteGrupoRubros");
		}return wb;
	}
	
	@DirectMethod
	public List<CodigosDto> llenaGrid(int noEmpresa){
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				System.out.println("Llega al action del buscar");
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				listaResultado = objCodigosService.llenaGrid(noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: llenaGrid");
		}return listaResultado;
	}
	
	
	@DirectMethod
	public String eliminaCodigo(int idRenglon, int idRubro ,String opcion){
		String mensajeRespuesta = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensajeRespuesta = objCodigosService.eliminaRegistro(idRenglon, idRubro, opcion);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: eliminaCodigo");
		}return mensajeRespuesta;
	}
	

//	@DirectMethod
//	public String eliminaCodigo(String idCodigo, int noEmpresa){
//		String mensajeRespuesta = "";
//		try{
//			objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
//			mensajeRespuesta = objCodigosService.eliminaRegistro(idCodigo, noEmpresa);
//		}
//		catch(Exception e){
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
//					"P: Utilerias, C: CodigosAction, M: eliminaCodigo");
//		}return mensajeRespuesta;
//	}
	
	
	@DirectMethod
	public String insertaCodigo(String idCodigo, String descCodigo, int noEmpresa){
		String mensajeRespuesta = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensajeRespuesta = objCodigosService.insertaCodigo(idCodigo, descCodigo, noEmpresa);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: insertaCodigo");
		}return mensajeRespuesta;
	}
	
	@DirectMethod
	public String insertaGrupo(String idCodigo, String descCodigo){
		System.out.println("Entra al action modif");
		String mensajeRespuesta = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensajeRespuesta = objCodigosService.insertaGrupo(idCodigo, descCodigo);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: insertaCodigo");
		}return mensajeRespuesta;
	}
	
	@DirectMethod
	public List<RubroDTO> getRubros(int idGrupo){
						
		List<RubroDTO> rubros = null;
				
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				rubros = objCodigosService.getRubros(idGrupo);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:getRubros");
		}
		return rubros;
	}
	
	
	@DirectMethod
	public String insertaRubro(String idGrupo, String idRubro, String descRubro, String ingresoEgreso){
		String mensajeRespuesta = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensajeRespuesta = objCodigosService.insertaRubro(idGrupo, idRubro,descRubro,ingresoEgreso);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: insertaCodigo");
		}return mensajeRespuesta;
	}
	
	@DirectMethod
	public List<RubroDTO> getGuiasContables(int noEmpresa){
						
		List<RubroDTO> rubros = null;
		
		objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				rubros = objCodigosService.getGuiasContables(noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Traspasos, C:TraspasosAction, M:llenarComboGrupo");
		}
		return rubros;
	}

	@DirectMethod
	public String insertaGuiaContable(String noEmpresa,String idGrupo, String idRubro, String cuentaContable){
		String mensajeRespuesta = "";
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensajeRespuesta = objCodigosService.insertaGuiaContable(noEmpresa,idGrupo, idRubro,cuentaContable);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosAction, M: insertaCodigo");
		}return mensajeRespuesta;
	}
	
	
	
	/********************************************************************+
	 * Luis Alfredo Serrato Montes de Oca
	 */
	
	
	
	@DirectMethod
	public List<CodigosDto> getPolizas(){
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				listaPolizas = objCodigosService.getPolizas();
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: getPolizas");
		}
		return listaPolizas;
	}
	
	@DirectMethod
	public String agregarPoliza(int idPoliza, String nombrePoliza){
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensaje = objCodigosService.agregarPoliza(idPoliza, nombrePoliza);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: getPolizas");
		}
		
		return mensaje;
	}

	@DirectMethod
	public String eliminarPoliza(int idPoliza){
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensaje = objCodigosService.eliminarPoliza(idPoliza);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: eliminarPoliza");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo){	
		List<GrupoDTO> grupo = null;
				
		try{
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				grupo = objCodigosService.llenarComboGrupo(idTipoGrupo);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Utilerias, C:TraspasosAction, M:llenarComboGrupo");
		}
		return grupo;
	}
	
	@DirectMethod
	public List<CodigosDto> obtenerPolizasSinAsignar(String idRubro){
		System.out.println("--->>> ENTRO");
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				listaPolizas = objCodigosService.obtenerPolizasSinAsignar(idRubro);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: obtenerPolizasSinAsignar");
		}
		
		return listaPolizas;
	}
	
	@DirectMethod
	public String asignarPolizas(String json, String idRubro){
		String mensaje = "";
			
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensaje = objCodigosService.asignarPolizas(json, idRubro);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: asignarPolizas");
		} 
		
		
		return mensaje;
	}
	
	@DirectMethod
	public String eliminarPolizas (String json, String idRubro){
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				mensaje = objCodigosService.eliminarPolizas(json, idRubro);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: eliminarPolizas");
		} 
		
		
		return mensaje;

	}
	
	@DirectMethod
	public List<CodigosDto> obtenerPolizasAsignadas(String idRubro){
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				objCodigosService = (CodigosService)contexto.obtenerBean("objCodigosBusinessImpl");
				listaPolizas = objCodigosService.obtenerPolizasAsignadas(idRubro);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Utilerias, C: CodigosAction, M: obtenerPolizasAsignadas");
		}
		
		return listaPolizas;
	}

}
