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
import com.webset.set.egresos.service.AplicacionDescuentosPropuestasService;
import com.webset.set.egresos.service.ConsultaPropuestasService;
import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.service.CartasChequesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.service.EquivalenciaBancosService;
import com.webset.utils.tools.Utilerias;


public class CartasChequesAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	CartasChequesService objCartasChequesService;
	
	@DirectMethod
	public List<CartasChequesDto> llenaBanco(String idEmpresa){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaBanco(idEmpresa);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaBanco");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CartasChequesDto> llenaEmpresa(String idClave){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaEmpresa(idClave);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaEmpresa");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CartasChequesDto> llenaProveedor(){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaProveedor();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaProveedor");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CartasChequesDto> llenaChequera(String idEmpresa, String idBanco){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaChequera(idEmpresa,idBanco);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaChequera");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CartasChequesDto> llenaSolicitante(){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			System.out.println("Actionsol");
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaSolicitante();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaSolicitante");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CartasChequesDto> llenaAutorizaciones(){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaAutorizaciones();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaAutorizaciones");
		}return listaResultado;
	}
	
	
	@DirectMethod
	public List<CartasChequesDto> llenaAutorizaciones2(){
		
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaAutorizaciones2();
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaAutorizaciones2");
		}return listaResultado;
	}
	
	
	
	@DirectMethod
	public List<CartasChequesDto> llenaClave(String fechaIni, String fechaFin){
		System.out.println(fechaIni+" a la fecha "+fechaFin);
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaClave(fechaIni,fechaFin);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: llenaClave");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();

		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = objCartasChequesService.llenarComboBeneficiario(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenarComboBeneficiario");	
		}
		return list;
	}
	
	@DirectMethod
	public List<CartasChequesDto> llenaGrid(String idEmpresa, String idClave, 
			 String tipo, String idBanco, String tipoC, String op, String idChequera){
		System.out.println("entra grid");
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();
		try{	
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.llenaGrid(idEmpresa,idClave, tipo, idBanco, tipoC, op , idChequera);
			}
		} 
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: ParametrosCartaAction, M: llenaGrid");
		}return listaResultado;
	}
	
	@DirectMethod
	public List<CartasChequesDto> obtieneDatos(String idEmpresa, String banco, String tipo){
		System.out.println("entro");
		List<CartasChequesDto> listaResultado = new ArrayList<CartasChequesDto>();		
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			System.out.println("Llega al action de obtiene");
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			listaResultado = objCartasChequesService.obtieneDatos(idEmpresa, banco, tipo);
			System.out.println(listaResultado.size() + " tama\u00f1o obtiene");
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: obtieneDatos");
		}return listaResultado;
	}
	
	@DirectMethod
				 
	public String generaPDF (String valor, String beneficiarios, String dif, String tipo, String fechaImp){
		System.out.println("Fecha de Imp: "+fechaImp);
		String mensaje = "";
		Gson gson = new Gson();
		List<Map<String, String>> valorGson = gson.fromJson(valor, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		List<Map<String, String>> valorBeneficiarios = gson.fromJson(beneficiarios, new TypeToken<ArrayList<Map<String, String>>> () {}.getType());
		try{
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			mensaje = objCartasChequesService.generaPDF(valorGson, valorBeneficiarios, dif, tipo, fechaImp);
			}
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: generaPDF");
		}
		return mensaje;
		
	}
	
	@DirectMethod
	public String certificarCheque(String jsonFolios){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String mensaje = "";
		System.out.println("Llegaron los Folios "+jsonFolios);
		List<String> listFolios = new ArrayList<String>();
		int t;
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(jsonFolios, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try {
			String folios = new String();
			for(t=0; t<datos.size(); t++) {
				folios=datos.get(t).get("folio")!=null?datos.get(t).get("folio") : "";
				listFolios.add(folios);
			}
			System.out.println("Llegaron los Folios lista "+listFolios);
			if (Utilerias.haveSession(WebContextManager.get())
					&& Utilerias.tienePermiso(WebContextManager.get(),201)) {
				objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
				mensaje = objCartasChequesService.certificarCheque(listFolios);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Impresion, C: CartasChequesAction, M: certificarCheques");
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
			+"P: Impresion, C: CartasChequesAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P: Impresion, C: CartasChequesAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P: Impresion, C: CartasChequesAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resp = "";
		
		if(!Utilerias.haveSession(WebContextManager.get())
				|| !Utilerias.tienePermiso(WebContextManager.get(), 201))
			return resp;
		
		try {
			objCartasChequesService = (CartasChequesService)contexto.obtenerBean("objCartasChequesBusinessImpl");
			resp = objCartasChequesService.exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) 
			+"P: Impresion, C: CartasChequesAction, M:exportaExcel");
		}
		return resp;
	}
	
		
}
