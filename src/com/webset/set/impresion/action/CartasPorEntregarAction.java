package com.webset.set.impresion.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.dto.CartasPorEntregarDto;
import com.webset.set.impresion.service.CartasChequesService;
import com.webset.set.impresion.service.CartasPorEntregarService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;
import com.webset.utils.tools.Utilerias;

public class CartasPorEntregarAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	CartasPorEntregarService CartasPorEntregar;
	CartasChequesService objCartasChequesService;
	
	
	@DirectMethod
	 
	public String generaPDF (String valor, String beneficiarios, String dif, String tipo, String fechaImp){
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> valorGson = gson.fromJson(valor, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		List<Map<String, String>> valorBeneficiarios = gson.fromJson(beneficiarios, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
				CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
				mensaje = CartasPorEntregar.generaPDF(valorGson, valorBeneficiarios, dif, tipo, fechaImp);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasPorEntregarAction, M: generaPDF");
		}
		return mensaje;
		
	}
	
	@DirectMethod
	public List<CartasPorEntregarDto> obtieneDatos(String idEmision, String idBanco){
		System.out.println("entro");
		List<CartasPorEntregarDto> listaResultado = new ArrayList<CartasPorEntregarDto>();		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
			System.out.println("Llega al action de obtiene");
			CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
			listaResultado = CartasPorEntregar.obtieneDatos(idEmision, idBanco);
			System.out.println(listaResultado.size() + " tama\u00f1o obtiene");
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: obtieneDatos");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CartasChequesDto> llenaBanco(){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaBancoSP();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaBanco");
		}return listaResultado;
	}
	
	
	@DirectMethod
	public List<CartasPorEntregarDto> obtenerCartasE(String folio, String idBanco, String tipo, String estatus,String fechaIni,String fechaFin){
		System.out.println("Entra action obtenerCartas: "+fechaIni+" "+fechaFin);
		List<CartasPorEntregarDto> listaCartas = new ArrayList<CartasPorEntregarDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
			CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
			listaCartas = CartasPorEntregar.obtenerCartasE(folio, idBanco, tipo, estatus,fechaIni,fechaFin);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarAction, M:obtenerCartasE");
		}
		
		return listaCartas;
	}
	
	public HSSFWorkbook reporteCartasEmitidas(String folio, String idBanco, 
			String tipo, String estatus,String fechaIni,String fechaFin,ServletContext context){
		HSSFWorkbook wb = null;
		try {
			CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl",context);
			wb = CartasPorEntregar.reporteCartasEmitidas(folio, idBanco,
					tipo, estatus, fechaIni, fechaFin);
		} catch (Exception e) {
			bitacora.insertarRegistro(
					new Date().toString() + " " + Bitacora.getStackTrace(e) + 
					"P: Impresion, C: CartasPorEntregarAction, "
					+ "M: reporteCartasEmitidas");
		}return wb;
	}
	
	@DirectMethod
	public List<CartasPorEntregarDto> obtenerCartas(String folio, String estatus,String tipo){
		System.out.println("Entra action obtenerCartas el tipo es: "+tipo);
		List<CartasPorEntregarDto> listaCartas = new ArrayList<CartasPorEntregarDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
			CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
			listaCartas = CartasPorEntregar.obtenerCartas(folio, estatus, tipo);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarAction, M:obtenerCartas");
		}
		
		return listaCartas;
	}
	
	@DirectMethod
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol){
		List<MantenimientoSolicitantesFirmantesDto> listaSolicitantes = new ArrayList<MantenimientoSolicitantesFirmantesDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
			CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
			listaSolicitantes = CartasPorEntregar.obtenerSolicitantes(tipoSol);
			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarAction, M:obtenerSolicitantes");
		}
		
		return listaSolicitantes;
	}
	
	@DirectMethod
	public String cambiarEstatus(String jsonDatos){
		System.out.println("entro a action cambiar");
		String mensaje = "";
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
			CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
			mensaje = CartasPorEntregar.cambiarEstatus(jsonDatos);
			}
		} catch (Exception e) {
			mensaje = "Error al obtener el contexto";
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarAction, M:cambiarEstatus");
		}
		
		return mensaje;
	}
	
	@DirectMethod
	public List<CartasPorEntregarDto> llenaFolio(String fechaIni, String fechaFin){
		System.out.println(fechaIni+" a la fecha "+fechaFin);
		List<CartasPorEntregarDto> listaResultado = new ArrayList<CartasPorEntregarDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
				CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
			listaResultado = CartasPorEntregar.llenaFolio(fechaIni,fechaFin);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarAction, M:llenaFolio");
		}return listaResultado;
	}
	
	@DirectMethod
	public String exportarExcel(String json){
		String mensaje = "";
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),202)) {
			CartasPorEntregar = (CartasPorEntregarService)contexto.obtenerBean("cartasPorEntregarBusinessImpl");
			mensaje = CartasPorEntregar.exportarExcel(json);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Impresion, C:CartasPorEntregarAction, M:exportarExcel");
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

	
}