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
import com.webset.set.utilerias.dao.EquivalenciaEmpresasDao;
import com.webset.set.utilerias.service.EquivalenciaEmpresasService;
import com.webset.set.utileriasmod.dto.EquivalenciaEmpresasDto;
import com.webset.utils.tools.Utilerias;

public class EquivalenciaEmpresasBusinessImpl implements EquivalenciaEmpresasService{
	
	EquivalenciaEmpresasDao objEquivalenciaEmpresasDao;
	Bitacora bitacora;

	public List<EquivalenciaEmpresasDto> llenaComboEmpresas(){
		return objEquivalenciaEmpresasDao.llenaComboEmpresas();
	}
	
	public List<EquivalenciaEmpresasDto> llenaGrid(String nomEmpresa){
		System.out.println("llega al busines");
		return objEquivalenciaEmpresasDao.llenaGrid(nomEmpresa);
		
	}
	
	public String validaDatos(List<Map<String, String>> registro){
		String mensaje = "";
		try{
			//Valida empresa Interface
			if (registro.get(0).get("empresaInterface").equals("")){
				mensaje = "Falta el nombre de la Empresa de Interface";
				System.out.println(mensaje + "interface");
			}
			
			//Valida empresa SET
			if(registro.get(0).get("empresaSet").equals("")){
				mensaje = "Falta seleccionar el n�mero de empresa";
				System.out.println(mensaje + "empresaSET");
			}
			
			//Valida la descripcion de la empresa SET
			if (registro.get(0).get("descripcion").equals("")){
				mensaje = "Falta seleccionar una empresa";
				System.out.println(mensaje + "descripcion");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Utilerias, C: EquivalenciaEmpresasBusinessImpl, M: validaDatos");
		}return mensaje;
	}
	
	public int eliminaRegistro(String codigo, String empresaSet, String empresaInterface){			
		return objEquivalenciaEmpresasDao.eliminaRegistro(codigo, empresaSet, empresaInterface);
	}
	
	public String insertaActualizaEmpresa(List<Map<String, String>> grid){
		List<EquivalenciaEmpresasDto> recibeDatos = new ArrayList<EquivalenciaEmpresasDto>();
		int resultadoEntero = 0;
		String mensajeRespuesta = "";
		
		//Se actualiza la informaci�n
		if (grid.get(0).get("tipoOperacion").equals("modificar"))
		{
			resultadoEntero = objEquivalenciaEmpresasDao.actualizaEmpresa(grid);
			if (resultadoEntero > 0)
				mensajeRespuesta = "El registro se actualizo correctamente";
			else
				mensajeRespuesta = "Ocurrion un problema durante la actualizaci�n";
		}
		else if (grid.get(0).get("tipoOperacion").equals("insertar"))
		{
			//SE valida si ya existe el registro en la Base de Datos 
			recibeDatos = objEquivalenciaEmpresasDao.buscaRegistro(grid);
			if (recibeDatos.size() > 0)
				mensajeRespuesta = "Los datos ya existen";
			else
			{
				//Si no existe el registro se inserta en la Base de Datos
				resultadoEntero = objEquivalenciaEmpresasDao.insertaEmpresa(grid);
				
				if (resultadoEntero > 0)
					mensajeRespuesta = "El registro se guardo correctamente";
				else
					mensajeRespuesta = "Ocurrion un error al guardar el registro";
			}
		}
		return mensajeRespuesta;	
	}
	
	//******************************************************************************************************************************
	public EquivalenciaEmpresasDao getObjEquivalenciaEmpresasDao() {
		return objEquivalenciaEmpresasDao;
	}

	public void setObjEquivalenciaEmpresasDao(
			EquivalenciaEmpresasDao objEquivalenciaEmpresasDao) {
		this.objEquivalenciaEmpresasDao = objEquivalenciaEmpresasDao;
	}

	@Override
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
														"Descripcion",
													 	"Empresa Set",
													 	"Empresa Interface",
													 	"Codigo"
												}, 
												parameters, 
												new String[]{
														"descripcion",
													 	"empresaSet",
													 	"empresaInterface",
													 	"codigo"
												});			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "EquivalenciaEmpresas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:EquivalenciaEmpresasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:EquivalenciaEmpresasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}

}
