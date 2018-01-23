package com.webset.set.seguridad.business;

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
import com.webset.set.seguridad.dao.EmpresaDao;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.seguridad.dto.UsuarioEmpresaDto;
import com.webset.set.seguridad.exceptions.BusinessException;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.utils.tools.Utilerias;

/**
 * 
 * @author vtello
 *
 */
public class EmpresaBusiness {
	private EmpresaDao empresaDao;
	Bitacora bitacora=new Bitacora();
	
	/**
	 * Busca en la BD un usuario cuyo id sea igual al parametro proporcionado
	 * @param id idetificador del usuario a buscar
	 * @return usuario cuyo id sea igual al id proporcionado como parametro
	 * @throws BusinessException en caso de un error logico (No encuentra al usuario, hay mas de 2 usuarios, etc) 
	 * @throws Exception en caso de un error de programacion 
	 */
	public List<EmpresaDto> obtenerListaEmpresas(int noUsuario, boolean existe) throws Exception, BusinessException{
		List<EmpresaDto> empresas = empresaDao.consultar(noUsuario, existe);
		return empresas;
	}
	
	public int insertar(UsuarioEmpresaDto dto, boolean todos){
		int res=0;
		try {
			res=empresaDao.insertar(dto,todos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public int  eliminar(UsuarioEmpresaDto dto, boolean todos) {
		int res=0;
		try {
			res=empresaDao.eliminar(dto, todos);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
	
	public List<EmpresaDto> obtenerTodasEmpresas() throws Exception, BusinessException{
		List<EmpresaDto> empresas = empresaDao.obtenerTodasEmpresas();
		return empresas;
	}
	
	/*
	 * Excel agregado por Yoseline E.C
	 * 03.03.16
	 */

	
	
	
	
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Id. Usuario",
													"Usuario",
													"No.empresa",
													"Empresa"
												}, 
												parameters, 
												new String[]{
														"noUsuario",
														"nombreUsuario",
														"noEmpresa",
														"nombreEmpresa"
														
												},"Empresas Asignadas");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:SegEmpresaAction, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:SegEmpresaAction, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	
	
	
		//getters && setters
	public EmpresaDao getEmpresaDao() {
		return empresaDao;
	}
	
	public void setEmpresaDao(EmpresaDao empresaDao) {
		this.empresaDao = empresaDao;
	}
	

}
