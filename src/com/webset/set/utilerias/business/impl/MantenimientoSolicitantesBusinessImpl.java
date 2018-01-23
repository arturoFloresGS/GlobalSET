package com.webset.set.utilerias.business.impl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dao.MantenimientoSolicitantesDao;
import com.webset.set.utilerias.service.MantenimientoSolicitantesService;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesDto;
import com.webset.utils.tools.Utilerias;



public class MantenimientoSolicitantesBusinessImpl implements MantenimientoSolicitantesService{
	private Bitacora bitacora = new Bitacora();
	private MantenimientoSolicitantesDao objMantenimientoSolicitantesDao;	
	
	public List<MantenimientoSolicitantesDto> llenaGridSolicitantes(String nombre){
		List<MantenimientoSolicitantesDto> recibeDatos = new ArrayList<MantenimientoSolicitantesDto>();
		try {
			recibeDatos = objMantenimientoSolicitantesDao.llenaGridSolicitantes(nombre);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesBusinessImpl, M:MantenimientoSolicitantesService");	
		}return recibeDatos;
	
	}
	
	public String insertSolicitante(MantenimientoSolicitantesDto dto){
		String resultado="";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objMantenimientoSolicitantesDao.insertSolicitante(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesBusinessImpl, M:insertSolicitante");	
		}return resultado;
	}
	
	public String updateSolicitante(MantenimientoSolicitantesDto dto){
		String resultado="";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objMantenimientoSolicitantesDao.updateSolicitante(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesBusinessImpl, M:updateSolicitante");	
		}return resultado;
	}
	
	public String deleteSolicitante(MantenimientoSolicitantesDto dto){
		String resultado="Error";
		try {
			if(!dto.getIdSolicitante().equals("")){
				resultado=objMantenimientoSolicitantesDao.deleteSolicitante(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:MantenimientoSolicitantesBusinessImpl, M:deleteSolicitante");	
		}return resultado;
	}
	
	public String validarDatos(MantenimientoSolicitantesDto dto){
		String res="";
		try {
			if(dto.getIdentificacion1().equals("")|| dto.getNombre1().equals("") || dto.getPuesto1().equals("")||dto.getIdentificacion2().equals("")|| dto.getNombre2().equals("") || dto.getPuesto2().equals("")){
				return "Error datos vacios";
			}else{
				if(dto.getCorreo1().equals("")||dto.getTelefono1().equals("")){
					return "Debe ingresar un correo y un tel�fono para el contacto 1";
				}else{
					if(dto.getCorreo2().equals("")||dto.getTelefono2().equals("")){
						return "Debe ingresar un correo y un tel�fono para el contacto 2";
					}else{
						if(!dto.getCorreo1().equals(""))
							res=validarCorreo(dto.getCorreo1(),"1");
						if(!res.equals(""))
							return res;
						if(!dto.getCorreo2().equals(""))
							res=validarCorreo(dto.getCorreo2(),"2");
						if(!res.equals(""))
							return res;
						if((!dto.getTelefono1().equals("") && dto.getTelefono1().length()<=9))
							res="Tel�fono no v�lido para el contacto 1,<br> se requieren m�nimo 10 d�gitos.";
						if((!dto.getTelefono2().equals("") && dto.getTelefono2().length()<=9))
							res="Tel�fono no v�lido para el contacto 2,<br> se requieren m�nimo 10 d�gitos.";
						if(!res.equals(""))
							return res;
						return res;	
					}
				}			
			}
		} catch (Exception e) {
			return "Error datos corruptos";
		}
	}
	
	private String validarCorreo(String correo, String contacto){
		int arroba=0;
		int punto=0;
		for (int i = 0; i < correo.length(); i++) {
			if(correo.charAt(i)=='@')
					arroba++;
			if(correo.charAt(i)=='.')
				punto++;
		}
		
		if(punto!=0 && arroba==1)
			return "";
		else
			return "Correo no v�lido para el contacto "+ contacto;
	}
	
	
	/*********Exporta excel************/
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
	    	
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Fecha alta",
													"Identificacion",
										            "Nombre",
										            "Puesto",
										            "Telefono",
										            "Correo",
										            "Identificacion 2",
										            "Nombre 2",
										            "Puesto 2",
										            "Telefono 2",
										            "Correo 2",
										            "Observacion"
												}, 
												parameters, 
												new String[]{
														"fecAlta",
														"idPersona1",
														"nombre1",
														"puesto1",
														"telefono1",
														"correo1",
														"idPersona2",
														"nombre2",
														"puesto2",
														"telefono2",
														"correo2",
														"observacion"
												,"Mantenimiento Solicitantes"},"Solicitantes ");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:MantenimientoSolicitantesBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:MantenimientoSolicitantesBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	//*********************************************************************
		public MantenimientoSolicitantesDao getMantenimientoSolicitantesDao() {
			return objMantenimientoSolicitantesDao;
		}

		public void setObjMantenimientoSolicitantesDao(
				MantenimientoSolicitantesDao objMantenimientoSolicitantesDao) {
			this.objMantenimientoSolicitantesDao = objMantenimientoSolicitantesDao;
		}
		
		

}
