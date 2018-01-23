package com.webset.set.egresos.action;

/** Luis Alfredo Serrato Montes de Oca
 * 20/01/2016
 **/

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
import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.service.AplicacionDescuentosPropuestasService;
import com.webset.set.egresos.service.PagoPropuestasService;
import com.webset.set.impresion.business.ChequesPorEntregarBusinessImpl;
import com.webset.set.impresion.service.ChequesPorEntregarService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class AplicacionDescuentosPropuestasAction {
	Bitacora bitacora = new Bitacora();
	Contexto contexto = new  Contexto();
	AplicacionDescuentosPropuestasService aplicacionDescuentos;
	
	
	
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboGrpEmpresas(String idUsuario){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<LlenaComboGralDto> listaGrupos = new ArrayList<LlenaComboGralDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
			listaGrupos = aplicacionDescuentos.llenarComboGrpEmpresas(idUsuario);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosPropuestasAction, M:llenarComboGrpEmpresas");
		}
		return listaGrupos;
	}
	
	
	@DirectMethod
	public List<PagosPendientesDto> obtenerPagos(String json){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<PagosPendientesDto> listaPagos = new ArrayList<PagosPendientesDto>();
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
			listaPagos = aplicacionDescuentos.obtenerPagos(json);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosPropuestasAction, M:obtenerPagos");
		}
		return listaPagos;
	}
	
	
	@DirectMethod
	public List<PagosPendientesDto> obtenerDetallePagos(String json, String jsonD){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<PagosPendientesDto> listDetalles = new ArrayList<PagosPendientesDto>();
		List<String> listP = new ArrayList<String>();
		List<String> listD = new ArrayList<String>();
		int t;
		int d;
		Gson gson = new Gson();
		Gson gsonD = new Gson();
		List<Map<String, String>> datos = gson.fromJson(json, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> datosD = gsonD.fromJson(jsonD, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		
		
		try {
			for(t=0; t<datos.size(); t++) {
				String claves = new String();
				claves=datos.get(t).get("cveControlPago")!=null?datos.get(t).get("cveControlPago") : "";
				listP.add(claves);
			}
			
			for(d=0; d<datosD.size(); d++) {
				String claves = new String();
				claves=datosD.get(d).get("cveControlDesc")!=null?datosD.get(d).get("cveControlDesc") : "";
				listD.add(claves);
			}
			if (Utilerias.haveSession(WebContextManager.get())) {
			aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
			System.out.println("Llego action "+listD);
			listDetalles = aplicacionDescuentos.obtenerDetallePagos(listP, listD);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosPropuestasAction, M:obtenerDetallePagos");
		}
		
		return listDetalles;
	}
	
	@DirectMethod
	public List<PagosPendientesDto> obtenerDescuentos(String jsonClaves,String noEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		System.out.println("Llego action "+jsonClaves+" noEmpresa: "+noEmpresa);
		List<PagosPendientesDto> listaDescuentos = new ArrayList<PagosPendientesDto>();
		List<String> listDes = new ArrayList<String>();
		int t;
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(jsonClaves, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		try {
			String claves = new String();
			for(t=0; t<datos.size(); t++) {
				claves=datos.get(t).get("cveControlPago")!=null?datos.get(t).get("cveControlPago") : "";
				listDes.add(claves);
			}
		
			if (Utilerias.haveSession(WebContextManager.get())) {
			aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
			listaDescuentos = aplicacionDescuentos.obtenerDescuentos(listDes,noEmpresa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosPropuestasAction, M:obtenerDescuentos");
		}
		return listaDescuentos;
	}
	
	@DirectMethod
	public String aplicarDescuentoSimple(String json, String cvePago, String jsonD,String jsonClaves){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		String mensaje = "";
		List<String> listPag = new ArrayList<String>();
		List<String> listD = new ArrayList<String>();
		int t;
		int d;
		Gson gson = new Gson();
		Gson gsonD = new Gson();
		List<Map<String, String>> datos = gson.fromJson(jsonClaves, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> datosD = gsonD.fromJson(jsonD, new TypeToken<ArrayList<Map<String,String>>>(){}.getType());
		try {
			String claves = new String();
			for(t=0; t<datos.size(); t++) {
				claves=datos.get(t).get("cveControlPago")!=null?datos.get(t).get("cveControlPago") : "";
				listPag.add(claves);
			}
			String clavesD = new String();
			for (d = 0; d < datosD.size(); d++) {
				clavesD=datosD.get(d).get("cveControlDesc")!=null?datosD.get(d).get("cveControlDesc") : "";
				listD.add(clavesD);
			}
			if (Utilerias.haveSession(WebContextManager.get())) {
			aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
			mensaje = aplicacionDescuentos.aplicarDescuentoSimple(json, cvePago, listD,listPag);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosPropuestasAction, M:aplicarDescuentoSimple");
		}
		
		return mensaje;
	}
	
	
	@DirectMethod
	public List<PagosPendientesDto> obtenerDescuentosDet(String jsonD, String ePersona, String numeroEmpresa){
		if (!Utilerias.haveSession(WebContextManager.get())) 
			return null;
		List<PagosPendientesDto> listDetalles = new ArrayList<PagosPendientesDto>();
		List<String> listD = new ArrayList<String>();
		int d;
		Gson gsonD = new Gson();
		List<Map<String, String>> datosD = gsonD.fromJson(jsonD, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		System.out.println("Llego al action "+jsonD+" "+ePersona+" "+numeroEmpresa );
		try {
			for(d=0; d<datosD.size(); d++) {
				String claves = new String();
				claves=datosD.get(d).get("cveControlDesc")!=null?datosD.get(d).get("cveControlDesc") : "";
				listD.add(claves);
			}
			if (Utilerias.haveSession(WebContextManager.get())) {
			aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
			listDetalles = aplicacionDescuentos.obtenerDetalleDesc(listD, ePersona, numeroEmpresa);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:AplicacionDescuentosPropuestasAction, M:obtenerPagos");
		}
		
		return listDetalles;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden, boolean regUnico){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		
		if(!Utilerias.haveSession(WebContextManager.get()))
			return list;
		
		LlenaComboGralDto dto= new LlenaComboGralDto();

		try{
			aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			dto.setRegistroUnico(regUnico);
			list = aplicacionDescuentos.llenarComboBeneficiario(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Impresion, C:AplicacionDescuentosPropuestasAction, M:llenarComboBeneficiario");	
		}
		return list;
	}

	//Revisado 
		@DirectMethod
		public List<LlenaComboGralDto>consultarProveedores(String texto){
			List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
			
			if(!Utilerias.haveSession(WebContextManager.get())
					|| !Utilerias.tienePermiso(WebContextManager.get(), 57))
				return list;
			
			try{
				aplicacionDescuentos = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness");
				list = aplicacionDescuentos.consultarProveedores(texto);
			}catch(Exception e){
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
				+"P:Impresion, C:AplicacionDescuentosPropuestasAction, M:consultarProveedores");
			}
			return list;
		}

		@DirectMethod
		public HSSFWorkbook consultaPropuestasDescuentos(String claveP,String claveD, ServletContext context){
			HSSFWorkbook wb=null;
			System.out.println("claveP"+claveP);
			System.out.println("claveD"+claveD);
			try {
				
				AplicacionDescuentosPropuestasService aplicacionDescuentosPropuestasService = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness",context);
				//objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl",context);
				
			wb=aplicacionDescuentosPropuestasService.consultaPropuestasDescuentos(claveP,claveD);
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C:AplicacionDescuentosPropuestasAction, M:consultaPropuestasDescuentos");
			}return wb;
		}
		
		@DirectMethod
		public HSSFWorkbook consultaHeader(String claveP,String claveD, ServletContext context){
			HSSFWorkbook wb=null;
			System.out.println("claveP"+claveP);
			System.out.println("claveD"+claveD);
			try {
				
				AplicacionDescuentosPropuestasService aplicacionDescuentosPropuestasService = (AplicacionDescuentosPropuestasService)contexto.obtenerBean("decuentosPropuestasBusiness",context);
				//objMantenimientoChequerasService = (MantenimientoChequerasService)contexto.obtenerBean("objMantenimientoChequerasBusinessImpl",context);
				
			wb=aplicacionDescuentosPropuestasService.consultaHeader(claveP,claveD);
			} catch (Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Personas, C:AplicacionDescuentosPropuestasAction, M:consultaHeader");
			}return wb;
		}
}
