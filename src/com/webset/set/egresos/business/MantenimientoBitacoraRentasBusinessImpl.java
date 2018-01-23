package com.webset.set.egresos.business;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.MantenimientoBitacoraRentasDao;
import com.webset.set.egresos.service.MantenimientoBitacoraRentasService;
import com.webset.set.egresos.dto.MantenimientoBitacoraRentasDto;



public class MantenimientoBitacoraRentasBusinessImpl implements MantenimientoBitacoraRentasService{
	private Bitacora bitacora= new Bitacora();
	private MantenimientoBitacoraRentasDao objMantenimientoBitacoraRentasDao;	
	
	public List<MantenimientoBitacoraRentasDto> llenaGridBitacoraRentas(String noEmpresa , String NoBeneficiario, String estatus){
		List<MantenimientoBitacoraRentasDto> recibeDatos = new ArrayList<MantenimientoBitacoraRentasDto>();
		try {
			recibeDatos = objMantenimientoBitacoraRentasDao.llenaGridBitacoraRentas(noEmpresa, NoBeneficiario, estatus);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: MantenimientoBitacoraRentasBusinessImpl, M: llenaGridBitacoraRentas");
		}return recibeDatos;
	
	}
	
	public String updateMantenimientoBitacoraRentas(List<MantenimientoBitacoraRentasDto> dtos){
		String resultado="Error al validar datos";
		try {
			if(dtos.size()!=0){
				resultado=objMantenimientoBitacoraRentasDao.updateMantenimientoBitacoraRentas(dtos);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: MantenimientoBitacoraRentasBusinessImpl, M: updateMantenimientoBitacoraRentas");
		}return resultado;
	}
	
	public List<LlenaComboGralDto> llenaComboEmpresa(String noUsuario){
		List<LlenaComboGralDto> recibeDatos = new ArrayList<LlenaComboGralDto>();
		try {
			recibeDatos = objMantenimientoBitacoraRentasDao.llenaComboEmpresa(noUsuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: MantenimientoBitacoraRentasBusinessImpl, M: llenaComboEmpresa");
		}return recibeDatos;
	}
	public List<LlenaComboGralDto> llenaComboProveedor(String nombre, String noProv){
		List<LlenaComboGralDto> recibeDatos = new ArrayList<LlenaComboGralDto>();
		try {
			if((nombre != null && !nombre.equals("")&& nombre.length()>3) || !noProv.equals("0") )
				recibeDatos = objMantenimientoBitacoraRentasDao.llenaComboProveedor(nombre, noProv);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: MantenimientoBitacoraRentasBusinessImpl, M: llenaComboProveedor");
		}return recibeDatos;
	}
	
	/*********Exporta excel************/
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			//Se crea el libro Excel
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"No.Empresa",
													"Empresa",
										            "Proveedor",
										            "No. Proveedor",
										            "Documento",
										            "Folio detalle",
										            "Fecha de rechazo",
										            "Importe",
										            "Tipo Gasto",
										            "Estatus",
												}, 
												parameters, 
												new String[]{
														"noEmpresa",
														"nomEmpresa",
														"noBeneficiario",
														"nomBeneficiario",
														"noDocumento",
														"noFolioDetalle",
														"fechaRechazo",	
														"importe",
														"idTipoGasto", 
														"estatus", 
												});			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:MantenimientoBitacoraRentasDaoImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	//*********************************************************************
		public MantenimientoBitacoraRentasDao getMantenimientoBitacoraRentasDao() {
			return objMantenimientoBitacoraRentasDao;
		}

		public void setObjMantenimientoBitacoraRentasDao(
				MantenimientoBitacoraRentasDao objMantenimientoBitacoraRentasDao) {
			this.objMantenimientoBitacoraRentasDao = objMantenimientoBitacoraRentasDao;
		}
		
		

}
