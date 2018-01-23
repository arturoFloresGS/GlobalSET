package com.webset.set.impresion.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.impresion.dao.MantenimientosDao;
import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.impresion.service.MantenimientosService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;

/*
 * Modificado el 07 de enero del 2016
 * Por Yoseline Espino C.
 */

public class MantenimientosBusinessImpl implements MantenimientosService {
	private Bitacora bitacora= new Bitacora();
	private MantenimientosDao mantenimientosDao;
	
	//Patanlla de Mantenimiento de Firmas
	
	public List<LlenaComboGralDto> llenarComboNoFirmantes(){
		List<LlenaComboGralDto> combo=  new ArrayList<LlenaComboGralDto>();
		try {
			combo= mantenimientosDao.llenarComboNoFirmantes();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:llenarComboNoFirmantes");
		}return combo;
	}
	
	public List<MantenimientosDto> buscaFirmantes() {
		List<MantenimientosDto> firmantes = new ArrayList<MantenimientosDto>();
		
		try {
			firmantes = mantenimientosDao.buscaFirmantes(0);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:buscaFirmantes");
		}
		return firmantes;
	}
	
	public Map<String, Object> eliminarFirmantes(List<MantenimientosDto> list){
		Map<String, Object> mapRetornoMsg = new HashMap<String, Object>();
		int resp;
		List<MantenimientosDto> existe = new ArrayList<MantenimientosDto>();
		
		try {
			if(list.size() <= 0) {
				mapRetornoMsg.put("msgUsuario", "Seleccione la firmante a eliminar");
				return mapRetornoMsg;
			}
			for(int i=0; i<list.size(); i++) {
				existe = mantenimientosDao.existeFirma(list.get(i).getNoPersona());
				
				if(existe.size() > 0) {
					mapRetornoMsg.put("msgUsuario", "El firmante es predeterminado, no se puede eliminar");
					return mapRetornoMsg;
				}else {
					resp = mantenimientosDao.eliminarFirmante(list.get(i).getNoPersona());
					
					if (resp == 0) {
						mapRetornoMsg.put("msgUsuario", "Error, al eliminar al firmante");
						return mapRetornoMsg;
					}
				}
			}
			mapRetornoMsg.put("msgUsuario", "Firmante Eliminado");
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:eliminarFirmantes");
		}
		return mapRetornoMsg;
	}
	
	public Map<String, Object> insertarFirmantes(List<MantenimientosDto> list) {
		Map<String, Object> mapRetorno = new HashMap<String, Object>();
		List<MantenimientosDto> buscaFirmante = new ArrayList<MantenimientosDto>();
		int resp = 0;
		
		try {
			for(int i=0; i<list.size(); i++) {
				if(list.get(i).getNoPersona() == 0) {
					mapRetorno.put("msgUsuario", "El No. de firma es requerido y mayor a 0");
					return mapRetorno; 
				}else if(list.get(i).getNombre().equals("")) {
					mapRetorno.put("msgUsuario", "El nombre del firmante es necesario");
					return mapRetorno; 
				}else if(list.get(i).getPathFirma().equals("")) {
					mapRetorno.put("msgUsuario", "La ubicaci�n de la firma es necesaria");
					return mapRetorno;
				}
				buscaFirmante = mantenimientosDao.buscaFirmantes(list.get(i).getNoPersona());
				
				if(buscaFirmante.size() > 0) {
					resp = mantenimientosDao.modificarFirmante(list.get(i).getNoPersona(), list.get(i).getNombre(), list.get(i).getPathFirma());
				}else {
					resp = mantenimientosDao.insertarFirmante(list.get(i).getNoPersona(), list.get(i).getNombre(), list.get(i).getPathFirma());
				}
			}
			if(resp > 0) mapRetorno.put("msgUsuario", "Datos Registrados");
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:insertarFirmantes");
		}
		return mapRetorno;
	}
	
	//Patanlla de Mantenimiento de Firmas
	
	public List<LlenaComboGralDto> llenaComboBancos(){
		List<LlenaComboGralDto> datos=  new ArrayList<LlenaComboGralDto>();
		try {
			datos = mantenimientosDao.llenaComboBancos();
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:llenaComboBancos");
		}
		return datos;
	}
	
	public List<LlenaComboGralDto> llenaComboChequeras(int idBanco) {
		List<LlenaComboGralDto> datos=  new ArrayList<LlenaComboGralDto>();
		try {
			if(idBanco!=0)
			datos = mantenimientosDao.llenaComboChequeras(idBanco);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:llenaComboChequeras");
		}
		return datos;
	}
	
	public List<LlenaComboGralDto> llenaComboPersonas(String idBanco , String cuenta, boolean busqueda){
		List<LlenaComboGralDto> datos=  new ArrayList<LlenaComboGralDto>();
		try {
			if(!idBanco.equals("")&&!cuenta.equals(""))
				datos = mantenimientosDao.llenaComboPersonas(idBanco,cuenta,busqueda);
			else
				if(busqueda)
					datos = mantenimientosDao.llenaComboPersonas(idBanco,cuenta,busqueda);
				
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:llenaComboPersonas");
		}
		return datos;
	}
	
	public List<MantenimientosDto> buscaFirmas(String idBanco, String idChequera, String noFirma) {
		List<MantenimientosDto> datos = new ArrayList<MantenimientosDto>();
		try {
			datos = mantenimientosDao.buscaFirmas( idBanco,  idChequera,  noFirma);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:buscaFirmas");
		}
		return datos;
	}
	
	public String eliminarFirma(MantenimientosDto dto){
		String mensaje="Error";
		try {
			mensaje = mantenimientosDao.eliminarFirma(dto);	
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:eliminarFirma");
		}return mensaje;
	}
	
	public String insetarFirma(MantenimientosDto dto){
		System.out.println("en el business");
		String mensaje="Error";
		try {
			if(dto.getIdBanco()!=0 && !dto.getIdChequera().equals("")&&dto.getNoPersona()!=0){
				mensaje=mantenimientosDao.insetarFirma(dto);
			}else{
				mensaje="Se debe seleccionar un banco, una cuenta y un n�mero de firma.";
			}
			
		} catch (Exception e) {
			System.out.println("error en el business");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:insetarFirma");
		}return mensaje;
	}
	
	/**********Excel***************/			 
	public String exportaExcel(String datos,String op) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String[] titulos=null,registros = null;
		String encabezado="Mantenimiento de";
	    try {	    	
	    	switch (op) {
			case "firmas":
				titulos= new String[]{"No.Firma","Nombre","Id banco","Banco" ,"Chequera"};
				registros=new String[]{"noPersona","nombre","idBanco","descBanco","idChequera"};
				encabezado+=" firmas. ";
				break;
			case "firmantes":
				titulos= new String[]{"No.Firma","Nombre","Ubicacion"};
				registros=new String[]{"noPersona","nombre","pathFirma"};
				encabezado+=" firmantes. ";
				break;
			}
	    	
			HSSFWorkbook wb = Utilerias.generarExcel(titulos, parameters, registros,encabezado);			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Impresion, C:MantenimientosBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	
	//get y set
	public MantenimientosDao getMantenimientosDao() {
		return mantenimientosDao;
	}
	public void setMantenimientosDao(MantenimientosDao mantenimientosDao) {
		this.mantenimientosDao = mantenimientosDao;
	}
}