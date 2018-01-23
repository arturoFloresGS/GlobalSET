package com.webset.set.egresos.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 * 22102015
 */


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webset.set.egresos.dao.RentasDao;
import com.webset.set.egresos.dto.DatosExcelDto;
import com.webset.set.egresos.service.RentasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.utils.tools.Utilerias;

public class RentasBusinessImpl implements RentasService {
	private RentasDao rentasDao;
	Bitacora bitacora = new Bitacora();
	GlobalSingleton gb = GlobalSingleton.getInstancia();
	
	@Override
	public List<DatosExcelDto> validarDatosExcel(String datos) {
		List<DatosExcelDto> datosExcelDtos = new ArrayList<DatosExcelDto>();
		try {
			datosExcelDtos = rentasDao.validarDatosExcel(datos);
		}catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasBusinessImpl, M:validarDatosExcel");
		}
		return datosExcelDtos;
	}
	
	
	@Override
	public String crearPropuesta(String matrizDatos, double totalAprovado, double totalAprovadoDls, int sociedad ) {
		StringBuffer claveControlMxn = new StringBuffer();
		StringBuffer conceptoMxn = new StringBuffer();
		StringBuffer claveControlDls = new StringBuffer();
		StringBuffer conceptoDls = new StringBuffer();
		int mxn = 0;
		int dls = 0;
		String mensaje = "Propuesta Generada ";
		SimpleDateFormat formato = new SimpleDateFormat("ddMMyyyy");
		
		try{
			int idGrupoFlujo = rentasDao.obtenerGrupoFlujo(sociedad);
			
			if(totalAprovado > 0){
				claveControlMxn.append("MR");
				claveControlMxn.append(rentasDao.obtenerFolioCupos(gb.getUsuarioLoginDto().getIdUsuario()));
				claveControlMxn.append(formato.format(gb.getFechaHoy()));
				claveControlMxn.append(gb.getUsuarioLoginDto().getIdUsuario());
				
				conceptoMxn.append("R-RENTAS-" + gb.getUsuarioLoginDto().getClaveUsuario());
				conceptoMxn.append("-" + idGrupoFlujo + "-");
				conceptoMxn.append(formato.format(gb.getFechaHoy()));
				conceptoMxn.append("-MN");
				mxn = rentasDao.insertarPropuestaPago(claveControlMxn.toString(), idGrupoFlujo, totalAprovado , gb.getFechaHoy(), conceptoMxn.toString());
				mensaje += "Clave MN: " + claveControlMxn.toString();
				
			}
			
			if(totalAprovadoDls > 0){
				
				claveControlDls.append("MR");
				claveControlDls.append(rentasDao.obtenerFolioCupos(gb.getUsuarioLoginDto().getIdUsuario()));
				claveControlDls.append(formato.format(gb.getFechaHoy()));
				claveControlDls.append(gb.getUsuarioLoginDto().getIdUsuario());
				
				conceptoDls.append("R-RENTAS-" + gb.getUsuarioLoginDto().getClaveUsuario());
				conceptoDls.append("-" + idGrupoFlujo + "-");
				conceptoDls.append(formato.format(gb.getFechaHoy()));
				conceptoDls.append("-DLS");
				dls = rentasDao.insertarPropuestaPago(claveControlDls.toString(), idGrupoFlujo, totalAprovadoDls, gb.getFechaHoy() , conceptoDls.toString());
				mensaje += "Clave DLS: " + claveControlDls.toString();
			}
			
			
			if(mxn == 1 || dls == 1){ 
				Gson gson = new Gson();
				List<Map<String, String>> datos = gson.fromJson(matrizDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
				
				for(int i = 0; i < datos.size(); i++){
					
					String total =  datos.get(i).get("total").replaceAll("\\$", "");
					
					if(datos.get(i).get("moneda").equals("MN") ){
						rentasDao.actualizaMvimiento(gb.getFechaHoy(), claveControlMxn.toString(), Integer.parseInt(datos.get(i).get("sociedad")), Integer.parseInt(datos.get(i).get("numeroAcredor")), datos.get(i).get("nombreAcredor"), datos.get(i).get("docSap"), datos.get(i).get("viaPago"), datos.get(i).get("banco"), datos.get(i).get("cuenta"), 
									datos.get(i).get("moneda"),Double.parseDouble(total.replaceAll("\\,", "")), datos.get(i).get("ccid"), datos.get(i).get("noFolioDet"),
									datos.get(i).get("referencia"),datos.get(i).get("bandera"));
						
					}else{
						rentasDao.actualizaMvimiento(gb.getFechaHoy(), claveControlDls.toString(), Integer.parseInt(datos.get(i).get("sociedad")), Integer.parseInt(datos.get(i).get("numeroAcredor")), datos.get(i).get("nombreAcredor"), datos.get(i).get("docSap"), datos.get(i).get("viaPago"), datos.get(i).get("banco"), datos.get(i).get("cuenta"), 
								datos.get(i).get("moneda"),Double.parseDouble(total.replaceAll("\\,", "")), datos.get(i).get("ccid"), datos.get(i).get("noFolioDet"),
								datos.get(i).get("referencia"),datos.get(i).get("bandera"));
					}
				}
			}			
			
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:RentasBusinessImpl, M:crearPropuesta");
		}
			
		
		
		return mensaje;
	}
	
	/**********Excel***************/			 
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
	    try {	    	
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"CCID",
													"Sociedad",
													"No.Acreedor",
													"Nombre",
													"Documento",
													"Via pago",
													"Banco",
													"Cuenta",
													"Divisa",
													"Fecha contabilizacion",
													"Importe",
													"observacion",
													"Referencia"
												}, 
												parameters, 
												new String[]{
														"ccid",
														"sociedad",
														"numeroAcredor",
														"nombreAcredor",
														"docSap",
														"viaPago",
														"banco",
														"cuenta",
														"moneda",
														"fechaContabilizacion",
														"total",
														"observacion",
														"referencia"
														
												});	
			
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:RentasBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Egresos, C:RentasBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	

	
	
	/******************************************/
	/********GETTERS AND SETTERS***************/
	/******************************************/
	
	
	public RentasDao getRentasDao() {
		return rentasDao;
	}
	public void setRentasDao(RentasDao rentasDao) {
		this.rentasDao = rentasDao;
	}
	
}
