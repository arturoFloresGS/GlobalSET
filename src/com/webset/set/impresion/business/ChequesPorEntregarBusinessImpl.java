package com.webset.set.impresion.business;

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
import com.webset.set.impresion.dao.ChequesPorEntregarDao;
import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.dto.ChequePorEntregarDto;
import com.webset.set.impresion.service.ChequesPorEntregarService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.ConstantesSet;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;

public class ChequesPorEntregarBusinessImpl implements ChequesPorEntregarService{
	private ChequesPorEntregarDao chequesPorEntregarDao;
	Bitacora bitacora = new Bitacora();
	
	
	
	public List<ChequePorEntregarDto> obtenerCheques(String json, String entregado){
		List<ChequePorEntregarDto> listaCheques = new ArrayList<ChequePorEntregarDto>();
		Gson gson = new Gson();
		
		try {
			List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			listaCheques = chequesPorEntregarDao.obtenerCheques(datos, entregado);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarBusinessImpl, M:obtenerCheques");
		}
		
		return listaCheques;
	}
	
	@Override
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol){
		List<MantenimientoSolicitantesFirmantesDto> listaSolcitantes = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		
		try {
			listaSolcitantes = chequesPorEntregarDao.obtenerSolicitantes(tipoSol);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarBusinessImpl, M:obtenerSolicitantes");
		}
		
		return listaSolcitantes;
	}
	
	@Override
	public String enviarDatos(String jsonDatos){
		String mensaje = "";
		Gson gson = new Gson();
		try {
			List<Map<String, String>> datos = gson.fromJson(jsonDatos, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
			mensaje = chequesPorEntregarDao.actualizarMovimiento(datos);
			
		} catch (Exception e) {
			mensaje = "Error en la convercion de datos";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarBusinessImpl, M:enviarDatos");
		}
		
		return mensaje;
	}
	
	@Override
	public String exportarExcel(String json){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> parameters = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());

		try {
			
			HSSFWorkbook wb = Utilerias.generarExcel(new String[]{
					"EMPRESA",
		            "NUMERO PROVEEDOR",
		            "NOMBRE PROVEEDOR",
		            "BANCO",
		            "CHEQUERA",
		            "NUMERO CHEQUE",
		            "IMPORTE",
		            "FECHA IMPRESION",
		            "DIVISA",
		            "ENTREGADO",
		            "FECHA ENTREGA",
		            "USUARIO ENTREGA"
				}, 
				parameters, 
				new String[]{
						"nomEmpresa", 
						"noProveedor",
						"nombreProveedor",
						"nombreBanco",
						"idChequera",
						"noCheque",														
						"importe", 
						"fecImprime",
						"divisa",
						"entregado",
						"fecEntregado",
						"usuEntregado"
				});		
			
			mensaje = ConstantesSet.RUTA_EXCEL + "cheques_entregar " + Utilerias.indicadorFecha() +".xls";
			File outputFile = new File(mensaje);
			FileOutputStream outs = new FileOutputStream(outputFile);
			wb.write(outs);
			outs.close();            
			
		} catch (IOException e) {
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Impresion, C:ChequesPorEntregarBusinessImpl, M:exportarExcel");
        	mensaje = "";
        } catch(Exception e){
        	bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
        	+ "P:Impresion, C:ChequesPorEntregarBusinessImpl, M:exportarExcel");
        	mensaje = "";
        
		}
		
		return mensaje;
	}
	//RCG
	@Override
	public List<ChequePorEntregarDto> llenaBanco(String idEmpresa) {
		List<ChequePorEntregarDto> resultado = new ArrayList<ChequePorEntregarDto>();
		try {
			resultado = chequesPorEntregarDao.llenaBanco(idEmpresa);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ChequesPorEntregarBusinessImpl, M: llenaBanco");
		}return resultado;
	}

	@Override
	public List<ChequePorEntregarDto> llenaEmpresa(){
		List<ChequePorEntregarDto> resultado = new ArrayList<ChequePorEntregarDto>();
		try {
			resultado = chequesPorEntregarDao.llenaEmpresa();
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ChequesPorEntregarBusinessImpl, M: llenaEmpresa");
		}return resultado;
	}
	
	@Override
	public List<ChequePorEntregarDto> llenaChequera(String idEmpresa, String idBanco) {
		List<ChequePorEntregarDto> resultado = new ArrayList<ChequePorEntregarDto>();
		try {
			resultado = chequesPorEntregarDao.llenaChequera(idEmpresa, idBanco);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ChequesPorEntregarBusinessImpl, M: llenaChequera");
		}return resultado;
	}

	/*@Override
	public List<LlenaComboGralDto> llenarComboBancos() {
		return chequesPorEntregarDao.llenarComboBancos();
	}*/
	
	/*public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto){
		return chequesPorEntregarDao.llenarComboGral(dto);
	}*/
	
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto){
		return chequesPorEntregarDao.llenarComboBeneficiario(dto);	
	}
	
	public List<LlenaComboGralDto>consultarProveedores(String texto){
		return chequesPorEntregarDao.consultarProveedores(texto);
	}
	/******************************************/
	/********GETTERS AND SETTERS***************/
	/******************************************/
	
	public ChequesPorEntregarDao getChequesPorEntregarDao() {
		return chequesPorEntregarDao;
	}
	public void setChequesPorEntregarDao(ChequesPorEntregarDao chequesPorEntregarDao) {
		this.chequesPorEntregarDao = chequesPorEntregarDao;
	}
	
	
}
