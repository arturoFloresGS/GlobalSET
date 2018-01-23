package com.webset.set.impresion.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.dto.ChequePorEntregarDto;
import com.webset.set.impresion.service.CartasChequesService;
import com.webset.set.impresion.service.ChequesPorEntregarService;
import com.webset.set.impresion.service.ImpresionService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;

public class ChequesPorEntregarAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	ChequesPorEntregarService chequesPorEntregar;
	
	@DirectMethod
	public List<ChequePorEntregarDto> obtenerCheques(String json, String entregado){
		List<ChequePorEntregarDto> listaCheques = new ArrayList<ChequePorEntregarDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
				listaCheques = chequesPorEntregar.obtenerCheques(json, entregado);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarAction, M:obtenerCheques");
		}
		
		return listaCheques;
	}
	
	@DirectMethod
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol){
		List<MantenimientoSolicitantesFirmantesDto> listaSolicitantes = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
				listaSolicitantes = chequesPorEntregar.obtenerSolicitantes(tipoSol);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarAction, M:obtenerSolicitantes");
		}
		
		return listaSolicitantes;
	}
	
	@DirectMethod
	public String enviarDatos(String jsonDatos){
		String mensaje = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
				mensaje = chequesPorEntregar.enviarDatos(jsonDatos);
			}
		} catch (Exception e) {
			mensaje = "Error al obtener el contexto";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarAction, M:enviarDatos");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public String exportarExcel(String json){
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())){
				chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
				mensaje = chequesPorEntregar.exportarExcel(json);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:ChequesPorEntregarAction, M:enviarDatos");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public HSSFWorkbook obtenerExcel(String nombre) {
		FileInputStream file = null;
		HSSFWorkbook workbook = null;
		File arch = new File(nombre);
		try {
			file = new FileInputStream(arch);
			workbook = new HSSFWorkbook(file);
			file.close();
		} catch (FileNotFoundException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	//RCG
	@DirectMethod
	public List<ChequePorEntregarDto> llenaEmpresa(){
		
		List<ChequePorEntregarDto> listaResultado = new ArrayList<ChequePorEntregarDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),195)) {
			chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
			listaResultado = chequesPorEntregar.llenaEmpresa();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ChequesPorEntregarAction, M:llenaEmpresa");
		}
		return listaResultado;
	}
	
	@DirectMethod
	public List<ChequePorEntregarDto> llenaBanco(String idEmpresa){
		
		List<ChequePorEntregarDto> listaResultado = new ArrayList<ChequePorEntregarDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),195)) {
				chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
			listaResultado = chequesPorEntregar.llenaBanco(idEmpresa);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ChequesPorEntregarAction, M:llenaBanco");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<ChequePorEntregarDto> llenaChequera(String idEmpresa, String idBanco){
		
		List<ChequePorEntregarDto> listaResultado = new ArrayList<ChequePorEntregarDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),195)) {
				chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
			listaResultado = chequesPorEntregar.llenaChequera(idEmpresa,idBanco);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ChequesPorEntregarAction, M:llenaChequera");
		}return listaResultado;
	}
	
	/*public List<LlenaComboGralDto>llenarComboGeneral(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list=chequesPorEntregar.llenarComboGral(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ChequesPorEntregarAction, M:llenarComboGeneral");	
		}
		return list;
	}*/
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		
		LlenaComboGralDto dto= new LlenaComboGralDto();

		try{
			chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = chequesPorEntregar.llenarComboBeneficiario(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:ChequesPorEntregarAction, M:llenarComboBeneficiario");	
		}
		return list;
	}

	//Revisado 
		@DirectMethod
		public List<LlenaComboGralDto>consultarProveedores(String texto){
			List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 195))
				return list;
			
			try{
				chequesPorEntregar = (ChequesPorEntregarService)contexto.obtenerBean("chequesPorEntregarBusinessImpl");
				list = chequesPorEntregar.consultarProveedores(texto);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Impresion, C:ChequesPorEntregarAction, M:consultarProveedores");
			}
			return list;
		}
	
}
