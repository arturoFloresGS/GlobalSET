package com.webset.set.utilerias.business.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dao.CodigosDao;
import com.webset.set.utilerias.service.CodigosService;
import com.webset.set.utileriasmod.dto.CodigosDto;
import com.webset.utils.tools.Utilerias;

public class CodigosBusinessImpl implements CodigosService{
	CodigosDao objCodigosDao;
	Bitacora bitacora;
	
	public List<CodigosDto> llenaComboEmpresas(){
		List<CodigosDto> listaResultado = new ArrayList<CodigosDto>();		
		try{
			listaResultado = objCodigosDao.llenaComboEmpresas();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: llenaComboEmpresas");
		}return listaResultado;
	}
	
	public List<CodigosDto> llenaGrid (int noEmpresa){		
		return objCodigosDao.llenaGrid(noEmpresa);
	}
	
	public String eliminaRegistro(int idRenglon, int idRubro, String codigo){
		String mensajeRespuesta = "";
		int recibeEntero = 0;
		
		try{
			recibeEntero = objCodigosDao.eliminaCodigo(idRenglon, idRubro ,codigo);
			if (recibeEntero > 0)
				mensajeRespuesta = "Registro Eliminado con Exito";
			else
				mensajeRespuesta = "Ocurrion un error durante el proceso";
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: eliminaRegistro");
		} return mensajeRespuesta;
	}
	
	public String insertaCodigo(String idCodigo, String descCodigo, int noEmpresa){
		String mensajeRespuesta = "";
		List<CodigosDto> recibeDatos = new ArrayList<CodigosDto>();
		int recibeEntero = 0;
		
		try{
			recibeDatos = objCodigosDao.buscaCodigo(noEmpresa, idCodigo);
			if (recibeDatos.size() > 0)
				mensajeRespuesta = "El C�digo ya existe";
			else{
				recibeEntero = objCodigosDao.insertaCodigo(noEmpresa, idCodigo, descCodigo);
				if (recibeEntero > 0)
					mensajeRespuesta = "C�digo guardado con Exito";
				else
					mensajeRespuesta = "Ocurrion un error durante el proceso";
			}
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: insertaCodigo");
		}return mensajeRespuesta;
	}
	
	
	public String insertaGrupo(String idGrupo, String descGrupo){
		
		String mensajeRespuesta = "";
		int recibeDatos = 0;
		int recibeEntero = 0;
		
		try{
			recibeDatos = objCodigosDao.buscaGrupo(idGrupo);
			if (recibeDatos > 0){
				recibeEntero = objCodigosDao.modificarGrupo(idGrupo, descGrupo);
				if (recibeEntero > 0)
					mensajeRespuesta = "Grupo modificado con Exito";
				else
					mensajeRespuesta = "Ocurrio un error durante el proceso";
			}
			else{
				recibeEntero = objCodigosDao.insertaGrupo(idGrupo, descGrupo);
				if (recibeEntero > 0){
					//objCodigosDao.insertarGrupoPoliza(idGrupo);
					mensajeRespuesta = "Grupo guardado con Exito";
				}
				else{
					mensajeRespuesta = "Ocurrio un error durante el proceso";
				}
			}
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: insertaCodigo");
		}return mensajeRespuesta;
	}
	
	//************************************************************************************************************************
	public CodigosDao getObjCodigosDao() {
		return objCodigosDao;
	}

	public void setObjCodigosDao(CodigosDao objCodigosDao) {
		this.objCodigosDao = objCodigosDao;
	}

	@Override
	public List<RubroDTO> getRubros(int idGrupo) {

		List<RubroDTO> listaResultado = new ArrayList<RubroDTO>();		
		try{
			listaResultado = objCodigosDao.getRubros(idGrupo);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: llenaComboEmpresas");
		}return listaResultado;

	}

	@Override
	public String insertaRubro(String idGrupo, String idRubro,String descRubro, String ingresoEgreso) {
		
		String mensajeRespuesta = "";
		int recibeDatos = 0;
		int recibeEntero = 0;
		
		try{
			recibeDatos = objCodigosDao.buscaRubro(idGrupo, idRubro);
			if (recibeDatos > 0){
				recibeEntero = objCodigosDao.modificarRubro(idGrupo,idRubro, descRubro,ingresoEgreso);
				if (recibeEntero > 0){
					mensajeRespuesta = "Rubro Modificado";
				}else{
					mensajeRespuesta = "Error al modificar el rubro";
				}
			}
			else{
				recibeEntero = objCodigosDao.insertaRubro(idGrupo,idRubro, descRubro,ingresoEgreso);
				if (recibeEntero > 0)
					mensajeRespuesta = "Grupo guardado con Exito";
				else
					mensajeRespuesta = "Ocurrio un error durante el proceso";
			}
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: insertaCodigo");
		}
		
		return mensajeRespuesta;
	}

	@Override
	public List<RubroDTO> getGuiasContables(int noEmpresa) {
		
		List<RubroDTO> listaResultado = new ArrayList<RubroDTO>();		
		try{
			listaResultado = objCodigosDao.getGuiasContables(noEmpresa);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: llenaComboEmpresas");
		}
		
		return listaResultado;
	}

	@Override
	public String insertaGuiaContable(String noEmpresa, String idGrupo,
									  String idRubro, String cuentaContable) {

		String mensajeRespuesta = "";
		int recibeDatos = 0;
		int recibeEntero = 0;
		
		try{
			recibeDatos = objCodigosDao.buscaGuiaContable(noEmpresa,idGrupo, idRubro,cuentaContable);
			if (recibeDatos > 0)
				mensajeRespuesta = "La guia contable ya existe";
			else{
				recibeEntero = objCodigosDao.insertaGuiaContable(noEmpresa,idGrupo, idRubro,cuentaContable);
				if (recibeEntero > 0)
					mensajeRespuesta = "Guia contable guardada con exito";
				else
					mensajeRespuesta = "Ocurrio un error durante el proceso";
			}
				
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: insertaCodigo");
		}
		
		return mensajeRespuesta;

		
	}
	/*
	 * Luis Alfredo Serrato Montes de Oca 
	 */

	@Override
	public List<CodigosDto> getPolizas(){
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		
		try {
			listaPolizas = objCodigosDao.getPolizas();
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: getPolizas");
		}
		
		return listaPolizas;
	}

	
	@Override
	public String eliminarPoliza(int idPoliza){
		String mensaje = "";
		
		try {
			mensaje = objCodigosDao.eliminarPoliza(idPoliza);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: eliminarPoliza");
		}
		
		return mensaje;
	}
	
	
	@Override
	public String agregarPoliza(int idPoliza, String nombrePoliza){
		String mensaje = "";
		
		try {
			
			if(objCodigosDao.existePoliza(idPoliza, nombrePoliza) > 0){
				mensaje = objCodigosDao.actualizarPoliza(idPoliza, nombrePoliza);
				
			}else{
				mensaje = objCodigosDao.agregarPoliza(idPoliza, nombrePoliza);
			}
			
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: agregarPoliza");
		}
		
		return mensaje;
	}
	
	@Override
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo) {
		List<GrupoDTO> grupos= null;
		try {
			grupos = objCodigosDao.llenarComboGrupo(idTipoGrupo);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: llenarComboGrupo");
		}
		return grupos;
		
	}//End methodo llenarComboRubro
	
	@Override
	public List<CodigosDto> obtenerPolizasSinAsignar(String idRubro){
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		
		try {
			listaPolizas = objCodigosDao.obtenerPolizasSinAsignar(idRubro);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: obtenerPolizasSinAsignar");
		}
		
		return listaPolizas;
	}
	
	@Override
	public String asignarPolizas(String json, String idRubro){
		String mensaje = "";		
		try {
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < datos.size(); i++){
				objCodigosDao.asignarPolizas(datos.get(i).get("idRubro"), idRubro);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: asignarPolizas");
		}
		
		return mensaje;
	}
	
	public String eliminarPolizas (String json, String idRubro){
		String mensaje = "";		
		try {
			Gson gson = new Gson();
			List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			
			for(int i = 0; i < datos.size(); i++){
				objCodigosDao.eliminarPolizas(datos.get(i).get("idRubro"), idRubro);
			}
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: asignarPolizas");
		}
		
		return mensaje;
	}



	@Override
	public List<CodigosDto> obtenerPolizasAsignadas(String idRubro){
		List<CodigosDto> listaPolizas = new ArrayList<CodigosDto>();
		
		try {
			listaPolizas = objCodigosDao.obtenerPolizasAsignadas(idRubro);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+ "P: Utilerias, C: CodigosBusinessImpl, M: obtenerPolizasAsignadas");
		}
		
		return listaPolizas;
	}

	@Override
	public HSSFWorkbook reporteGrupos() {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Id. Grupo",
					"Descripci�n",
					
							
			}, 
					objCodigosDao.reporteGrupos(), 
					new String[]{	
							"idGrupo",
							"descGrupo",
								
					});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: reporteGrupos");
		}return hb;
	}

	@Override
	public HSSFWorkbook reporteGrupoPolizas() {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Id. Poliza",
					"Descripci�n",
				
			}, 
					objCodigosDao.reporteGrupoPolizas(), 
					new String[]{	
							"idCodigo",
							"descCodigo",
								
					});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: reporteGrupoPoliza");
		}return hb;
	}

	@Override
	public HSSFWorkbook reporteGrupoRubros() {
		HSSFWorkbook hb=null;
		try {
			hb=Utilerias.generarExcel(new String[]{
					"Id. Grupo",
					"Descripci�n",
					"Id Rubro",
					"Descripcion",
					"I/E",
					
				
			}, 
					objCodigosDao.reporteGrupoRubros(), 
					new String[]{	
							"idGrupo",
							"descGrupo",
							"idRubro",
							"descRubro",
							"ingresoEgreso",
								
					});
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: CodigosBusinessImpl, M: reporteGrupoRubros");
		}return hb;
	}

	
}
