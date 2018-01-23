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
import com.webset.utils.tools.Utilerias;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.ConsultaSolicitudPagoSinPolizaDao;
import com.webset.set.egresos.service.ConsultaSolicitudPagoSinPolizaService;
import com.webset.set.egresos.dto.ConsultaSolicitudPagoSinPolizaDto;



public class ConsultaSolicitudPagoSinPolizaBusinessImpl implements ConsultaSolicitudPagoSinPolizaService{
	Bitacora bitacora;
	ConsultaSolicitudPagoSinPolizaDao objConsultaSolicitudPagoSinPolizaDao;	
	
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaGrid(String noEmpresa, String fechaIni, String fechaFin, String usuario,boolean facultad, String usuarioEnCurso){
		List<ConsultaSolicitudPagoSinPolizaDto> recibeDatos = new ArrayList<ConsultaSolicitudPagoSinPolizaDto>();
		try {
			recibeDatos = objConsultaSolicitudPagoSinPolizaDao.llenaGrid(noEmpresa, fechaIni,fechaFin, usuario,facultad,usuarioEnCurso);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: ConsultaSolicitudPagoSinPolizaImpl, M: llenaGrid");
			
		} return recibeDatos;
		
	}
	
	public List<ConsultaSolicitudPagoSinPolizaDto> llenaComboUsuario(boolean facultad, String usuario){
		List<ConsultaSolicitudPagoSinPolizaDto> recibeDatos = new ArrayList<ConsultaSolicitudPagoSinPolizaDto>();
		try {
			recibeDatos = objConsultaSolicitudPagoSinPolizaDao.llenaComboUsuario( facultad,  usuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C: ConsultaSolicitudPagoSinPolizaImpl, M: llenaComboUsuario");
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
													"Empresa",
													"Fecha pago",
										            "Beneficiario",
										            "Concepto",
										            "Monto",
										            "No.Documento",
										            "Usuario",
												}, 
												parameters, 
												new String[]{
														"nomEmpresa",
														"fechaPago",
														"beneficiario",
														"concepto",
														"importe",
														"noDocumento",	
														"usuario",
												},"Consulta solicitud pago sin poliza contable. ");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaSolicitudPagoSinPolizaDaoImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:ConsultaSolicitudPagoSinPolizaDaoImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	
	
	//*********************************************************************
		public ConsultaSolicitudPagoSinPolizaDao getConsultaSolicitudPagoSinPolizaDao() {
			return objConsultaSolicitudPagoSinPolizaDao;
		}

		public void setObjConsultaSolicitudPagoSinPolizaDao(
				ConsultaSolicitudPagoSinPolizaDao objConsultaSolicitudPagoSinPolizaDao) {
			this.objConsultaSolicitudPagoSinPolizaDao = objConsultaSolicitudPagoSinPolizaDao;
		}
		
		

}
