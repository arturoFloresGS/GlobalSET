package com.webset.set.utilerias.business.impl;
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
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dao.ConfiguracionSolicitudPagoDao;
import com.webset.set.utilerias.service.ConfiguracionSolicitudPagoService;
import com.webset.set.utileriasmod.dto.ConfiguracionSolicitudPagoDto;
import com.webset.utils.tools.Utilerias;



public class ConfiguracionSolicitudPagoBusinessImpl implements ConfiguracionSolicitudPagoService{
	private Bitacora bitacora = new Bitacora();
	private ConfiguracionSolicitudPagoDao objConfiguracionSolicitudPagoDao ;	
	
	public List<ConfiguracionSolicitudPagoDto> llenaGridBeneficiarios(String idPoliza, String idGrupo, String idRubro,String beneficiarios){
		List<ConfiguracionSolicitudPagoDto> recibeDatos = new ArrayList<ConfiguracionSolicitudPagoDto>();
		try {
			recibeDatos = objConfiguracionSolicitudPagoDao.llenaGridBeneficiarios(idPoliza,idGrupo,idRubro,beneficiarios);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:llenaGridBeneficiarios");	
		}return recibeDatos;
	
	}
	
	public List<ConfiguracionSolicitudPagoDto> llenaGridPolizas() {
		List<ConfiguracionSolicitudPagoDto> recibeDatos = new ArrayList<ConfiguracionSolicitudPagoDto>();
		try {
			recibeDatos = objConfiguracionSolicitudPagoDao.llenaGridPolizas();
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:llenaGridPolizas");	
		}return recibeDatos;
	}
	
	public List<ConfiguracionSolicitudPagoDto> llenaComboPoliza(String numUsuario){
		List<ConfiguracionSolicitudPagoDto> recibeDatos = new ArrayList<ConfiguracionSolicitudPagoDto>();
		try {
			recibeDatos = objConfiguracionSolicitudPagoDao.llenaComboPoliza(numUsuario);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:llenaComboPoliza");	
		}return recibeDatos;
	}
	
	public List<ConfiguracionSolicitudPagoDto> llenaComboGrupo(String idRubro){
		
		List<ConfiguracionSolicitudPagoDto> recibeDatos = new ArrayList<ConfiguracionSolicitudPagoDto>();
		try {
			recibeDatos = objConfiguracionSolicitudPagoDao.llenaComboGrupo(idRubro);
		} catch (Exception e) {
			
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:llenaComboGrupo");	
		}return recibeDatos;
	}
	public List<ConfiguracionSolicitudPagoDto> llenaComboRubro(String idPoliza){
		List<ConfiguracionSolicitudPagoDto> recibeDatos = new ArrayList<ConfiguracionSolicitudPagoDto>();
		try {
			recibeDatos = objConfiguracionSolicitudPagoDao.llenaComboRubro(idPoliza);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:llenaComboRubro");	
		}return recibeDatos;
	}
	
	public String existeConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="";
		try {
			if(dto.getIdPoliza().equals("")&& dto.getIdGrupo().equals("")&& dto.getIdRubro().equals("")){
				return "Error datos vacios";
			}else{
				resultado=objConfiguracionSolicitudPagoDao.existeConfiguracionSolicitudPago(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:existeConfiguracionSolicitudPago");	
		}return resultado;
	}
	
	public ConfiguracionSolicitudPagoDto consultarConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		ConfiguracionSolicitudPagoDto resultado=new ConfiguracionSolicitudPagoDto();
		try {
				resultado=objConfiguracionSolicitudPagoDao.consultarConfiguracionSolicitudPago(dto);
			
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:consultarConfiguracionSolicitudPago");	
		}return resultado;
	}
	
	public String insertaConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objConfiguracionSolicitudPagoDao.insertaConfiguracionSolicitudPago(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error business");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:insertaConfiguracionSolicitudPago");	
		}return resultado;
		
	}
	
	public String updateConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="";
		try {
			resultado=validarDatos(dto);
			if(resultado.equals("")){
				resultado=objConfiguracionSolicitudPagoDao.updateConfiguracionSolicitudPago(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:updateConfiguracionSolicitudPago");	
		}return resultado;
	}
	
	public String deleteConfiguracionSolicitudPago(ConfiguracionSolicitudPagoDto dto){
		String resultado="";
		try {
			if(dto.getIdPoliza().equals("")&& dto.getIdGrupo().equals("")&& dto.getIdRubro().equals("")){
				return "Error datos vacios";
			}else{
				resultado=objConfiguracionSolicitudPagoDao.deleteConfiguracionSolicitudPago(dto);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:deleteConfiguracionSolicitudPago");	
		}return resultado;
	}
	
	//Metodos para beneficiarios:
	
	public String existenBeneficiarios(String[] beneficiarios){
			String resultado="Error al cargar archivo";
			try {
				resultado=objConfiguracionSolicitudPagoDao.existenBeneficiarios(beneficiarios);
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:existenBeneficiarios");	
			}return resultado;
		}
	
	public String guardarBeneficiarios(String[] beneficiarios, ConfiguracionSolicitudPagoDto dto){
		String resultado="Error al cargar archivo";
		try {
			resultado=objConfiguracionSolicitudPagoDao.guardarBeneficiarios(beneficiarios,dto);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:guardarBeneficiario");	
		}return resultado;
	}
	//fin metodos beneficiarios
		
	public String validarDatos(ConfiguracionSolicitudPagoDto dto){
		try {
			if(dto.getIdPoliza().equals("")&& dto.getIdGrupo().equals("")&& dto.getIdRubro().equals("") && dto.getTexto().equals("")&& dto.getTextoCabecera().equals("")&& dto.getAsignacion().equals("")&& dto.getReferencia().equals("")&& dto.getReferenciaPago().equals("")&&  dto.getCentroCostos().equals("")&& dto.getDivision().equals("")&& dto.getOrden().equals("")&& dto.getBancoInterlocutor().equals("")&& dto.getFechaPago().equals("")&& dto.getObservaciones().equals("")&&  dto.getDivisaPago().equals("")&& dto.getAreaDestino().equals("")){
				return "Error datos vacios";
			}else{
				return "";
			}
		} catch (Exception e) {
			return "Error al validar los datos";
		}
	}
	
	/**********Excel***************/			 
	public String exportaExcel(String datos) {
		String respuesta = "";
	    Gson gson = new Gson();
		List<Map<String, String>> parameters1 = gson.fromJson(datos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> parameters = new ArrayList<Map<String, String>>();
		for (int i = 0; i < parameters1.size(); i++) {
			
			List<ConfiguracionSolicitudPagoDto> listaBenf=objConfiguracionSolicitudPagoDao.llenaGridBeneficiarios(parameters1.get(i).get("idPoliza"), parameters1.get(i).get("idGrupo"), parameters1.get(i).get("idRubro"), "");
			String benef="";
			for (int j = 0; j < listaBenf.size(); j++) {
				benef = benef + listaBenf.get(j).getNombreBeneficiario() + ";";
			}
			Map<String,String> map = new HashMap<>();
			map.putAll(parameters1.get(i));
			map.put("beneficiariosSelect", benef);
			parameters.add(map);
		}
		
	    try {	    	
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
													"Id Poliza",
													"Descripcion poliza",
													"Id grupo",
													"Descripcion grupo",
													"Id rubro",
													"Descripcion rubro",
													"Texto",
													"Texto cabecera",
													"Asignacion",
													"Referencia",
													"Referencia pago",
													"Division",
													"Centro costos",
													"Orden",
													"Fecha pago",
													"Observaciones",
													"Clase documento",
													"Transaccion",
													"Pago sin poliza",
													"Sociedad Gl",
													"Cheque caja",
													"Cheque",
													"Transferencia",
													"Cargo cuenta",
													"Banco Interlocutor",
													"Beneficiarios"
												}, 
												parameters, 
												new String[]{
														"idPoliza",
														"descPoliza",
														"idGrupo",
														"descGrupo",
														"idRubro",
														"descRubro",
														"texto",
														"textoCabecera",
														"asignacion",
														"referencia",
														"referenciaPago",
														"division",
														"centroCostos",
														"orden",
														"fechaPago",
														"observaciones",
														"claseDoc",
														"transaccion",
														"pagoSinPoliza",
														"sociedadGL",
														"chequeCaja",
														"cheque",
														"transferencia",
														"cargoCuenta",
														"bancoInterlocutor",
														"beneficiariosSelect"
												},"Parametros solicitud pago");			
            
            respuesta = ConstantesSet.RUTA_EXCEL + "propuestas " + Utilerias.indicadorFecha() +".xls";
            File outputFile = new File(respuesta);
          
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
        } catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:exportaExcel");
        	respuesta = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Utilerias, C:ConfiguracionSolicitudPagoBusinessImpl, M:exportaExcel");
            respuesta = "";
        }
        return respuesta;
	}
	
	
	//*********************************************************************
		public ConfiguracionSolicitudPagoDao getConfiguracionSolicitudPagoDao() {
			return objConfiguracionSolicitudPagoDao;
		}

		public void setObjConfiguracionSolicitudPagoDao(
				ConfiguracionSolicitudPagoDao objConfiguracionSolicitudPagoDao) {
			this.objConfiguracionSolicitudPagoDao = objConfiguracionSolicitudPagoDao;
		}

		
		
		

}
