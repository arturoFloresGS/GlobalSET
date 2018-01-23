package com.webset.set.egresos.business;

import java.util.ArrayList;
import java.util.Date;

/**
 * 20/01/2016
 * @author Luis Alfredo Serrato Montes de Oca
 **/

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.AplicacionDescuentosPropuestasDao;
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.service.AplicacionDescuentosPropuestasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

public class AplicacionDescuentosPropuestasBusinessImpl implements AplicacionDescuentosPropuestasService {
	private AplicacionDescuentosPropuestasDao decuentosPropuestasDao;
	Bitacora bitacora = new Bitacora();
	
	
	@Override
	public List<LlenaComboGralDto> llenarComboGrpEmpresas(String idUsuario) {
		List<LlenaComboGralDto> listaGrupos = new ArrayList<LlenaComboGralDto>();
		
		try {
			listaGrupos = decuentosPropuestasDao.llenarComboGrpEmpresas(idUsuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosBusinessImpl, M:llenarComboGrpEmpresas");
		}
		
		return listaGrupos;
	}
	
	@Override
	public List<PagosPendientesDto> obtenerPagos(String json){
		List<PagosPendientesDto> listPagos = new ArrayList<PagosPendientesDto>();
		Gson gson = new Gson();
		try {
			List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String,String>>>() {}.getType());
			listPagos = decuentosPropuestasDao.obtenerPagos(datos);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosBusinessImpl, M:obtenerPagos");
		}
		
		return listPagos;
	}
	
	
	@Override
	public List<PagosPendientesDto> obtenerDetallePagos(List<String> claves, List<String> clavesD){
		List<PagosPendientesDto> listDetalles = new ArrayList<PagosPendientesDto>();
		try {
			StringBuffer folios = new StringBuffer();
			for (int i = 0; i < claves.size(); i++) {
				if(!claves.get(i).equals("")){
					folios.append("'"+Utilerias.validarCadenaSQL(claves.get(i))+"',");
					//System.out.println(folios);
				}
			}
			if(folios.length()>0){
				folios.delete(folios.length()-1, folios.length());
			}
			StringBuffer foliosD = new StringBuffer();
			for (int i = 0; i < clavesD.size(); i++) {
				if(!clavesD.get(i).equals("")){
					foliosD.append("'"+Utilerias.validarCadenaSQL(clavesD.get(i))+"',");
					//System.out.println(folios);
				}
			}
			if(foliosD.length()>0){
				foliosD.delete(foliosD.length()-1, foliosD.length());
			}
			System.out.println("Llego Bussines: "+foliosD);
			listDetalles = decuentosPropuestasDao.obtenerDetallePagos(folios.toString(), foliosD.toString());
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosBusinessImpl, M:obtenerDetallePagos");
		}
		
		return listDetalles;
	}
	
	@Override
	public List<PagosPendientesDto> obtenerDescuentos(List<String> claves, String noEmpresa){
		List<PagosPendientesDto> listaDescuentos = new ArrayList<PagosPendientesDto>();
		System.out.println("Llego business "+claves);
		try {
			StringBuffer clavesDes = new StringBuffer();
			for (int i = 0; i < claves.size(); i++) {
				System.out.println("Llego for buss");
				if(!claves.get(i).equals("")){
					System.out.println("Llego if buss");
					clavesDes.append("'"+Utilerias.validarCadenaSQL(claves.get(i))+"',");
				}
			}
			clavesDes.delete(clavesDes.length()-1, clavesDes.length());
			
			listaDescuentos = decuentosPropuestasDao.obtenerDescuentos(clavesDes.toString(),noEmpresa);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosBusinessImpl, M:obtenerDescuentos");
		}
		
		return listaDescuentos;
	}
	
	public String aplicarDescuentoSimple(String json, String cvePago, List<String> clavesD,List<String> claves){
		StringBuffer mensaje = new StringBuffer();
		Gson gson = new Gson();
		String folios="";
		try {
			StringBuffer clavesPagos = new StringBuffer();
			StringBuffer clavePago = new StringBuffer();
			
			if(!claves.get(0).equals("")){
				clavePago.append("'"+Utilerias.validarCadenaSQL(claves.get(0))+"'");
			}
			for (int i = 1; i < claves.size(); i++) {
				if(!claves.get(i).equals("")){
					clavesPagos.append("'"+Utilerias.validarCadenaSQL(claves.get(i))+"',");
				}
			}
			if(clavesPagos.length()>0){
				clavesPagos.delete(clavesPagos.length()-1, clavesPagos.length());
			}
			if(clavesPagos.length()>0){
				int resAct = decuentosPropuestasDao.actualizarMovimiento(clavesPagos.toString(),clavePago.toString());
				if(resAct>0){
					System.out.println("Modificacion realizada con exito");					
					int resBorra = decuentosPropuestasDao.eliminarSeleccionAut(clavesPagos.toString());
					if(resBorra>0){
						System.out.println("Eliminacion realizada con exito");
					}else{
						System.out.println("Error al eliminar en seleccion_automatica_grupo");
					}		
				}else{
					System.out.println("Error al actualizar movimiento");
				}
			}
			StringBuffer foliosD = new StringBuffer();
			for (int i = 0; i < clavesD.size(); i++) {
				if(!clavesD.get(i).equals("")){
					foliosD.append("'"+Utilerias.validarCadenaSQL(clavesD.get(i))+"',");
					
				}
			}
			if(foliosD.length()>0){
				foliosD.delete(foliosD.length()-1, foliosD.length());
			}
			//Aqui pasar todos los movimientos de las otras propuestas a la primer propuesta y borrar las propuestas 
			//de pago que pasamos y actualizar el importe suma nadamas las 3000 
			//Movimientos actualizo y seleccion elimino update solo a 3000
			//Update,importe y nadamas 
			List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String,String>>>() {}.getType());
			folios = creaCadenaFolios(datos);
			int res = decuentosPropuestasDao.modificarMovimiento(folios, clavePago.toString(), foliosD.toString());
			if(res > 0){
				int resP = decuentosPropuestasDao.modificarPopuestaPago(foliosD.toString() ,clavePago.toString() ,  folios);
				System.out.println(resP+" resP");
				if(resP > 0){
					System.out.println(foliosD+" FoliosD");
					String[] cadenas = foliosD.toString().split(",");
					int resD=0;
					for (int i = 0; i < cadenas.length; i++) {
						resD =decuentosPropuestasDao.modificarPropuestaDescuento(cadenas[i], folios);
					}
					if(resD>0){
						mensaje.append("Descuento aplicado con exito");
					}else{
						mensaje.append("Ocurrio un error al aplicar el descuento");
					}
				}else{
					mensaje.append("Ocurrio un error al actualizar el total de pago " + cvePago);
				}
			}else{
				mensaje.append("No se pudo agregar el descuento al pago seleccionado ");
			}

		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosBusinessImpl, M:aplicarDescuentoSimple");
		}
		
		return mensaje.toString();
	}
	
	


	@Override
	public List<PagosPendientesDto> obtenerDetalleDesc(List<String> clavesD, String ePersona, String numeroEmpresa) {
		List<PagosPendientesDto> listDetalles = new ArrayList<PagosPendientesDto>();
		
		try {
			StringBuffer foliosD = new StringBuffer();
			for (int i = 0; i < clavesD.size(); i++) {
				if(!clavesD.get(i).equals("")){
					foliosD.append("'"+Utilerias.validarCadenaSQL(clavesD.get(i))+"',");
					//System.out.println(folios);
				}
			}
			if(foliosD.length()>0){
				foliosD.delete(foliosD.length()-1, foliosD.length());
			}
			listDetalles = decuentosPropuestasDao.obtenerDetalleDesc(foliosD.toString(), ePersona, numeroEmpresa);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosBusinessImpl, M:obtenerDetallePagos");
		}
		
		return listDetalles;
	}
	
	private String creaCadenaFolios(List<Map<String, String>> datos){
		String folios = "";
		
		for(int i = 0; i < datos.size(); i++){
			folios = folios + datos.get(i).get("folioDet");
			if(i != datos.size() -1){
				folios = folios + ", ";
			}
		}
		
		return folios;
	}
	
	private String creaCadenaFoliosDetPagos(List<Map<String, String>> datos){
		String folios = "";
		
		for(int i = 0; i < datos.size(); i++){
			folios = folios + datos.get(i).get("cveControl");
			if(i != datos.size() -1){
				folios = folios + ", ";
			}
		}
		
		return folios;
	}
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		return decuentosPropuestasDao.llenarComboBeneficiario(dto);	
	}
	
	public List<LlenaComboGralDto>consultarProveedores(String texto){
		return decuentosPropuestasDao.consultarProveedores(texto);
	}
	

	@Override
	public HSSFWorkbook consultaPropuestasDescuentos(String claveP, String claveD) {
	System.out.println(claveP);
		HSSFWorkbook hb=null;
		try {
			if(claveD.length()>0){
				claveD=claveD.substring(0,claveD.length()-1);
			}
			hb=Utilerias.generarExcel(new String[]{
					"Clave Control",
					"Beneficiario",					
					"Importe",	
			}, 
					decuentosPropuestasDao.consultaMovimientos(claveP,claveD), new String[]{
					"cve_control",
					"beneficiario",
					"importe",
			},
					"Detalle Propuestas");
						
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C:AplicacionDescuentosBusinessImpl, M: consultaPropuestasDescuentos");
		}
		return hb;
	}
	
	@Override
	public HSSFWorkbook consultaHeader(String claveP, String claveD) {
		HSSFWorkbook hb = null;
		try {
			if(claveD.length()>0){
				claveD=claveD.substring(0,claveD.length()-1);
			}
			
			hb = Utilerias.generarExcel(new String[]{
					"Clave Control",
					"Fecha de Propuesta",
					"Concepto",
					"Cupo Total",
			}, 
					decuentosPropuestasDao.consultaHeader(claveP,claveD), new String[]{
					"cve_control",
					"fecha_propuesta",
					"concepto",
					"cupo_total",
			},
					"Seleccion Automatica Grupo");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C:AplicacionDescuentosBusinessImpl, M: consultaHeader");
		}
		return hb;
	}	
	/******************************************/
	/********GETTERS AND SETTERS***************/
	/******************************************/

	public AplicacionDescuentosPropuestasDao getDecuentosPropuestasDao() {
		return decuentosPropuestasDao;
	}

	public void setDecuentosPropuestasDao(AplicacionDescuentosPropuestasDao decuentosPropuestasDao) {
		this.decuentosPropuestasDao = decuentosPropuestasDao;
	}

}


