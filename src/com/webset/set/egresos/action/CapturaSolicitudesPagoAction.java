
/**
 * @autor Cristian Garcia Garcia
 */

package com.webset.set.egresos.action;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.egresos.dto.PolizaContableDto;
import com.webset.set.egresos.service.CapturaSolicitudesPagoService;
import com.webset.set.egresos.service.PagosPedientesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.utils.tools.Utilerias;

public class CapturaSolicitudesPagoAction {
	
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();
	//List<Boolean> resultPerfil;// = new ArrayList<Boolean>();
	String sLetras = " ";
	int iCen = 0;
	int iDes = 0;
	int iUni = 0;
	
	@DirectMethod
	public Map<String, Object> generarDocumento(String data, String parametro, String cabecera){
		
		List<Boolean> resultPerfil = new ArrayList<Boolean>();
		Map<String,Object>ret= new HashMap<String,Object>();
		ret.put("msgUsuario", "Error desconocido");
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Gson gson1 = new Gson();
		List<Map<String, String>> objParamsParametro = gson1.fromJson(parametro, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Gson gson2 = new Gson();
		List<Map<String, String>> objParamscabecera = gson2.fromJson(cabecera, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		
		try {
			if(Utilerias.haveSession(WebContextManager.get())&& Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				/*resultPerfil = habilitaComponetesPerfilEmpresa(objParamsParametro.get(0).get("i0"), objParamsParametro.get(0).get("i1"), objParamsParametro.get(0).get("i2"));
				//System.out.println("tama�o de la lista perfiles "+resultPerfil.size());
				for (int i = 0; i < resultPerfil.size()-1; i++) {
					//System.out.println("parametro uno for "+objParams.get(0).get("i"+i)+" posision "+i);
			 		if (resultPerfil.get(i) && objParams.get(0).get("i"+i).equals("") ) {
					 		ret.put("msgUsuario", "Complete los campos del perfil seleccionado");
							return ret;
					} 
				}*/
				//
				System.out.println("objParams "+objParams);
				System.out.println("objParamsParametro "+objParamsParametro);
				System.out.println("objParamsCabecera "+objParamscabecera);
				
				System.out.println("1");
				if(Double.parseDouble(objParams.get(0).get("i4") != null ? objParams.get(0).get("i4").replace(",", "") : "0") == 0) {
			 		ret.put("msgUsuario", "Ingrese el importe de pago");
					return ret;
				}else{
					objParams.get(0).put("i4", objParams.get(0).get("i4").replace(",", ""));
				}
				System.out.println("2");
				
				
				System.out.println("3");
				if(Double.parseDouble(objParamscabecera.get(0).get("importeOriginal") != null ? objParamscabecera.get(0).get("importeOriginal").replace(",", "") : "0") == 0) {
			 		ret.put("msgUsuario", "Ingrese el importe de pago");
					return ret;
				}else{
					objParamscabecera.get(0).put("importeOriginal", objParamscabecera.get(0).get("importeOriginal").replace(",", ""));
				}
				System.out.println("4");
				if(objParamscabecera.get(0).get("idEmpresa").equals("")) {
					ret.put("msgUsuario", "Seleccione la empresa");
					return ret;
				}
				System.out.println("5");
				/*if(objParamscabecera.get(0).get("tipoPoliza").equals("")) {
					ret.put("msgUsuario", "Seleccione una poliza");
					return ret;
				}
				System.out.println("6");
				if(objParamscabecera.get(0).get("idGrupo").equals("")) {
					ret.put("msgUsuario", "Seleccione un grupo");
					return ret;
				}
				System.out.println("7");
				System.out.println(objParamscabecera.get(0).get("idRubro")+" nuevo");
			 	if(objParamscabecera.get(0).get("idRubro").equals("")) {
			 		ret.put("msgUsuario", "Seleccione un rubro");
					return ret;
				}*/
			 	System.out.println("8");
				if(objParamscabecera.get(0).get("idPersona").equals("")) {
					ret.put("msgUsuario", "Seleccione el beneficiario");
					return ret;
				}
				System.out.println("9");
			 	if(objParamscabecera.get(0).get("idFormaPago").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la forma de pago");
					return ret;
				}
			 	System.out.println("10");
			 	if(objParamscabecera.get(0).get("idDivOriginal").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la divisa original");
					return ret;
				}
			 	System.out.println("11");
			 	if(objParamscabecera.get(0).get("fechaFactura").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la fecha de la factura");
					return ret;
				}
			 	System.out.println("12");
			 	if(Double.parseDouble(objParamscabecera.get(0).get("importeOriginal") != null ? objParamscabecera.get(0).get("importeOriginal").replace(",", "") : "0") == 0) {
			 		ret.put("msgUsuario", "Ingrese el importe original");
					return ret;
				}
			 	System.out.println("13");
			 	
			 	return ret = capturaSolicitudesPagoService.generarDocumento(objParams.get(0), objParamscabecera.get(0));
			}	 	
		} catch (Exception e) {
			ret.put("msgUsuario", "Complete los campos.");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:generarDocumento");
		}
		return ret;
	}
	
	@DirectMethod
	public List<Boolean> habilitaComponetesPerfilEmpresa(String rubro, String poliza, String grupoRubro){
		List<Boolean> result = new ArrayList<Boolean>();
		try {
			
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				result = capturaSolicitudesPagoService.habilitaComponetesPerfilEmpresa(rubro, poliza, grupoRubro);
				//System.out.println("En el metodo que la signo valor"+result);
				//resultPerfil = result;
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:habilitaComponetesPerfilEmpresa");	
		}
		return result;
	}
	
	
	@DirectMethod
	public List<String> fijaPlantilla(String idPlantilla, String idUsuario){
		List<String> result = new ArrayList<String>();
		try {
			
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				result =  capturaSolicitudesPagoService.fijaPlantilla(Integer.parseInt(idPlantilla), Integer.parseInt(idUsuario));
				if (result.size()!=0) 
					return result;
				else 
					return result;
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:fijaPlantilla");
			e.printStackTrace();
			
		}
		return result;
		
	}

	@DirectMethod
	public String consultarConfiguraSet(int indice){
		String valor="";
		try{
			if (!Utilerias.haveSession(WebContextManager.get())&& !Utilerias.tienePermiso(WebContextManager.get(),50))
				return null;
			CapturaSolicitudesPagoService	capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
			valor = capturaSolicitudesPagoService.consultarConfiguraSet(indice);
		
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:consultarConfiguraSet");			
		}
		return valor;
	}
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario)
	{
		List<LlenaComboEmpresasDto>list=new ArrayList<LlenaComboEmpresasDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				list = capturaSolicitudesPagoService.obtenerEmpresas(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerEmpresas");			
		}
		return list;
	}
	
	@DirectMethod
	public List<String> obtenerGrupoDelRubro(String idRubro){
		List<String> result = new ArrayList<String>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				result = capturaSolicitudesPagoService.obtenerGrupoDelRubro(idRubro);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerGrupoDelRubro");			
		}
		return result;
	}
	
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaComboDivisaDto> obtenerDivisas(){
		List<LlenaComboDivisaDto>list=new ArrayList<LlenaComboDivisaDto>();
		try{
		//	if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				list = capturaSolicitudesPagoService.obtenerDivisas();
//			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerDivisas");			
		}
		return list;
	}
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaFormaPagoDto> obtenerFormaPago(){
		List<LlenaFormaPagoDto>list= new ArrayList<LlenaFormaPagoDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				list = capturaSolicitudesPagoService.obtenerFormaPago();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerDivisas");			
		}
		return list;
	}
	@DirectMethod
	public List<LlenaFormaPagoDto>llenarComboFormaPagoParametrizado(String poliza, String rubro, String Grupo){
		List<LlenaFormaPagoDto>list= new ArrayList<LlenaFormaPagoDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				list = capturaSolicitudesPagoService.llenarComboFormaPagoParametrizado(poliza, rubro, Grupo);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenarComboFormaPagoParametrizado");			
		}
		return list;
	}
	
	//En este js no se utiliza
	@DirectMethod
	public String obtenerFechaHoy(){
		String fechaString="";
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				fechaString = capturaSolicitudesPagoService.obtenerFechaHoy().toString();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerFechaHoy");			
		}
		return fechaString;
	}
	//revisado A.A.G.
	@DirectMethod
	public List<CajaUsuarioDto> obtenerCajaUsuario(int idUsuario){
		List<CajaUsuarioDto>listCajaUsuario= new ArrayList<CajaUsuarioDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				listCajaUsuario = capturaSolicitudesPagoService.obtenerCajaUsuario(idUsuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerCajaUsuario");	
		}
		return listCajaUsuario;
	}
	//mandado llamar para grupo 
	//mandado llamar para cat_area
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaComboGralDto>llenarCombo(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		System.out.println("condicion "+condicion);
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
				dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
				dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
				dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
				dto.setOrden(orden!=null && !orden.equals("")?orden:"");
				list=capturaSolicitudesPagoService.LlenarComboGral(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenaCombo");	
		}
		return list;
	}
	@DirectMethod
	public List<PolizaContableDto>llenarComboPoliza(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<PolizaContableDto> list= new ArrayList<PolizaContableDto>();
		PolizaContableDto dto= new PolizaContableDto();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
				dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
				dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
				dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
				dto.setOrden(orden!=null && !orden.equals("")?orden:"");
				list=capturaSolicitudesPagoService.llenarComboPoliza(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenaCombo");	
		}
		return list;
	}
	
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiario(String campoUno, String campoDos, String tabla, String condicion, String orden,String idStr,boolean regUnico, int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
				dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
				dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
				dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
				dto.setOrden(orden!=null && !orden.equals("")?orden:"");
				dto.setIdStr(idStr!=null && !idStr.equals("")?idStr:"");
				dto.setRegistroUnico(regUnico);
				list=capturaSolicitudesPagoService.llenarComboBeneficiario(dto, noEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenarComboBeneficiario");	
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBeneficiarioParametrizado(String campoUno, String campoDos, String tabla, String condicion, String orden,String poliza,String grupo, String rubro,boolean regUnico, int noEmpresa){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				System.out.println("entro a parametrizado");
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
				dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
				dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
				dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
				dto.setOrden(orden!=null && !orden.equals("")?orden:"");
				dto.setIdStr(poliza!=null && !poliza.equals("")?poliza:"");
				dto.setRegistroUnico(regUnico);
				list=capturaSolicitudesPagoService.llenarComboBeneficiarioParametrizado(dto, noEmpresa, grupo, rubro);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenarComboBeneficiario");	
		}
		return list;
	}
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboAreaDestino(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
				dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
				dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
				dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
				dto.setOrden(orden!=null && !orden.equals("")?orden:"");
				list = capturaSolicitudesPagoService.llenarComboAreaDestino(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenarComboAreaDestino");	
		}
		return list;
	}
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaComboGralDto>obtenerBancoBenef(String nacExt, String provUnico, String idPersona,int idEmpresa, String idDivisa){
		Map<String,Object>datos= new HashMap<String,Object>();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		System.out.println("entro a obteenrBanco"+ idPersona);
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				datos.put("nacExt",nacExt);
				datos.put("provUnico",provUnico);
				datos.put("idPersona",idPersona);
				datos.put("noEmpresa",552);//Esta es una empresa fija del sistema
				datos.put("idDivisa",idDivisa);	
				list = capturaSolicitudesPagoService.obtenerBancoBenef(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerBancoBenef");
		}
		return list;
	}
	//revisado A.A.G.
	@DirectMethod
	public List<LlenaComboGralDto>obtenerChequeras(String idPersona,int idEmpresa, int idBanco){
		Map<String,Object>datos= new HashMap<String,Object>();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				datos.put("idPersona",idPersona);
				datos.put("idEmpresa",idEmpresa);
				datos.put("idBanco",idBanco);
				list = capturaSolicitudesPagoService.obtenerChequeras(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerChequeras");
		}
		return list;
	}
	
	@DirectMethod
	public List<Map<String, String>>obtenerChequerasBancoInterlocutor(String idPersona,int idEmpresa, int idBanco, String idDivisa){
		Map<String,Object>datos= new HashMap<String,Object>();
		List<Map<String, String>> list= new ArrayList<Map<String, String>>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				datos.put("idPersona",idPersona);
				datos.put("idEmpresa",idEmpresa);
				datos.put("idBanco",idBanco);
				datos.put("idDivisa",idDivisa);
				list = capturaSolicitudesPagoService.obtenerChequerasBancoInterlocutor(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerChequeras");
		}
		return list;
	}
	//revisado A.A.G.
	@DirectMethod
	public List<Map<String, Integer>> obtenerSucPlazClabe(int idPersona, int idEmpresa, String idChequera){
		Map<String, Object> datos= new HashMap<String, Object>();
		List<Map<String, Integer>> list= new ArrayList<Map<String, Integer>>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				datos.put("idPersona", idPersona );
				datos.put("idEmpresa", idEmpresa);
				datos.put("idChequera", idChequera);
				list = capturaSolicitudesPagoService.obtenerSucPlazClabe(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerSucPlazClabe");
		}
		return list;
	}
	// no se utiliza para este sistema de GS
	@DirectMethod
	public List<MovimientoDto> consultarMovimientos(String noDocto, int idEmpresa, String psEquivale){
		Map<String, Object> datos=new HashMap<String, Object>();
		List<MovimientoDto> list= new ArrayList<MovimientoDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				datos.put("noDocto", noDocto);
				datos.put("idEmpresa", idEmpresa);
				datos.put("psEquivale", !psEquivale.trim().equals("") && !psEquivale.trim().equals("0")?psEquivale:"");
				list = capturaSolicitudesPagoService.consultarMovimientos(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:consultarMovimientos");	
		}
		return list;
	}
	//// no se utiliza para este sistema de GS
	@DirectMethod
	public List<Map<String, Object>> obtenerDetalleMovimiento(int noFolioDet){
		List<Map<String, Object>> list= new ArrayList<Map<String, Object>>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				list=capturaSolicitudesPagoService.obtenerDetalleMovimiento(noFolioDet);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerDetalleMovimiento");	
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> registrarPlantilla(String data){
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String, Object> ret = new HashMap<String,Object>();
		ret.put("msgUsuario", "Error desconocido.");
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				if(objParams.get(0).get("tipoPoliza").equals("")) {
					ret.put("msgUsuario", "Seleccione una poliza");
					return ret;
				}
				if(objParams.get(0).get("idGrupo").equals("")) {
					ret.put("msgUsuario", "Seleccione un grupo");
					return ret;
				}
			 	if(objParams.get(0).get("idRubro").equals("")) {
			 		ret.put("msgUsuario", "Seleccione un rubro");
					return ret;
				}
			 	if(objParams.get(0).get("idRubro").equals("")) {
			 		ret.put("msgUsuario", "Seleccione un rubro");
					return ret;
				}
			 	if(objParams.get(0).get("nomPlantilla").equals("")) {
			 		ret.put("msgUsuario", "Ingrese un nombre de plantilla");
					return ret;
				}
				return ret = capturaSolicitudesPagoService.registrarPlantilla(objParams.get(0));
			}	
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:registrarPlantilla");
		}
		return ret;
	}
	
	/*
	 * modificado 
	 * Alberto Antonio G. 22/09/2015.
	 * */
	@DirectMethod
	public Map<String, Object> ejecutarSolicitud(String data){
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String, Object> ret = new HashMap<String,Object>();
		
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				
				if(objParams.get(0).get("idEmpresa").equals("")) {
					ret.put("msgUsuario", "Seleccione la empresa");
					return ret;
				}
				if(objParams.get(0).get("idPersona").equals("")) {
					ret.put("msgUsuario", "Seleccione el beneficiario");
					return ret;
				}
			 	if(objParams.get(0).get("idFormaPago").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la forma de pago");
					return ret;
				}
			 	if(objParams.get(0).get("idDivOriginal").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la divisa original");
					return ret;
				}
			 	if(objParams.get(0).get("fechaFactura").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la fecha de la factura");
					return ret;
				}
			 	if(objParams.get(0).get("fechaPago").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la fecha de pago");
					return ret;
				}
			 	if(Double.parseDouble(objParams.get(0).get("importeOriginal") != null ? objParams.get(0).get("importeOriginal").replace(",", "") : "0") == 0) {
			 		ret.put("msgUsuario", "Ingrese el importe original");
					return ret;
				}
			 	if(objParams.get(0).get("concepto").equals("")) {
			 		ret.put("msgUsuario", "Ingrese el concepto");
					return ret;
				}
			 	if(objParams.get(0).get("idDivPago").equals("")) {
			 		ret.put("msgUsuario", "Seleccione la divisa de pago");
					return ret;
				}
			 	if(Integer.parseInt(objParams.get(0).get("tipoCambio")) == 0) {
			 		ret.put("msgUsuario", "Ingrese el tipo de cambio");
					return ret;
				}
			 	if(Double.parseDouble(objParams.get(0).get("importePago") != null ? objParams.get(0).get("importePago").replace(",", "") : "0") == 0) {
			 		ret.put("msgUsuario", "Ingrese el importe de pago");
					return ret;
				}
			 	
			 	if(objParams.get(0).get("clabe") != null) {
				 	if(objParams.get(0).get("clabe").equals("") && objParams.get(0).get("clabe").equals("3")) {
				 		ret.put("msgUsuario", "Ingrese la clabe");
						return ret;
					}
			 	}
			 	
			 	
			 	if( objParams.get(0).get("solicita") != null ){
			 		
				 	if(objParams.get(0).get("solicita").equals("")) {
				 		ret.put("msgUsuario", "Ingrese el solicitante");
						return ret;
					}
				 	
			 	}
			 	
			 	if( objParams.get(0).get("autoriza") != null ){
			 	
				 	if(objParams.get(0).get("autoriza").equals("")) {
				 		ret.put("msgUsuario", "Ingrese quien autoriza");
						return ret;
					}
			 	}
			 	//aqui
			 	/*if((Double.parseDouble(objParams.get(0).get("importeOriginal").replace(",", "")) < Double.parseDouble(objParams.get(0).get("sumImporte").replace(",", ""))) ||
			 			(Double.parseDouble(objParams.get(0).get("importeOriginal").replace(",", "")) > Double.parseDouble(objParams.get(0).get("sumImporte").replace(",", "")))){
			 		ret.put("msgUsuario", "La suma del importe debe ser igual al importe original");
					return ret;
				}*/
			 	//nuevos Alberto Antonio G.
			 	if( objParams.get(0).get("referencia") != null ){
				 	
				 	if(objParams.get(0).get("referencia").equals("")) {
				 		ret.put("msgUsuario", "Ingrese quien referencia");
						return ret;
					}
			 	}  
			 	
			 	if( objParams.get(0).get("cabecera") != null ){
				 	
				 	if(objParams.get(0).get("cabecera").equals("")) {
				 		ret.put("msgUsuario", "Ingrese quien cabecera");
						return ret;
					}
			 	}
			 	
				if( objParams.get(0).get("descripcion") != null ){
				 	
				 	if(objParams.get(0).get("descripcion").equals("")) {
				 		ret.put("msgUsuario", "Ingrese quien descripcion");
						return ret;
					}
			 	}
				
				if( objParams.get(0).get("asignacion") != null ){
				 	
				 	if(objParams.get(0).get("asignacion").equals("")) {
				 		ret.put("msgUsuario", "Ingrese quien asignacion");
						return ret;
					}
			 	}
				//ret.put("msgUsuario", "Exito al validar");
				//return ret;
			 	//for(int i = 0; i<objGrid.size(); i++)
					//logger.info("importe action " + i + " : " + objGrid.get(i).get("importe").replace(",", ""));
					
				return ret = capturaSolicitudesPagoService.ejecutarSolicitud(objParams.get(0));
			}	
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:ejecutarSolicitud");	
		}
		return ret;
	}
	//revisado A.A.G.
	@DirectMethod
	public Map<String, Double> obtenerCambioDivisa(String idDivisaOriginal,
			String idDivisaPago, double importeOriginal){
		
		Map<String, Double> res= new HashMap<String, Double>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = 
						(CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				res = capturaSolicitudesPagoService.obtenerCambioDivisa
						(idDivisaOriginal, idDivisaPago, importeOriginal);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerValorDivisa");	
		}
		return res;
	}
	//no se utiliza para en caso de GS
	@DirectMethod
	public String obtenerSolicitante() {
		String resp = "";
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = 
						(CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				resp = capturaSolicitudesPagoService.obtenerSolicitante();
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerSolicitante");
		}
		return resp;
	}
	//Al avence no se a ocupado pero existe la posibilidad de ocuparce
	@DirectMethod
	public String letrasCantidad(String sDivisa, String sImpCad) {
		String resp = "";
		int iBillon = 0;
		String sEtiqueta = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				if(!sDivisa.equals("MN")) {
					sImpCad = funciones.ponerCeros(sImpCad, 18);
					iBillon = Integer.parseInt(sImpCad.substring(0, 3));
					
					if(iBillon > 0) {
						if(iBillon == 1)
							sEtiqueta = "ONE BILLION ";
						else {
							sEtiqueta = "BILLIONS ";
							iCen = Integer.parseInt(sImpCad.substring(0, 1));
							iDes = Integer.parseInt(sImpCad.substring(1, 2));
							iUni = Integer.parseInt(sImpCad.substring(2, 3));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					} 
					//Busca mil millones
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(3, 6));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "THOUSAND MILLIONS ";
						else {
							sEtiqueta = "THOUSAND MILLIONS ";
							iCen = Integer.parseInt(sImpCad.substring(3, 4));
							iDes = Integer.parseInt(sImpCad.substring(4, 5));
							iUni = Integer.parseInt(sImpCad.substring(5, 6));
							calculaCentenasMN();
						}
						iBillon = Integer.parseInt(sImpCad.substring(6, 9));
						
						if(iBillon > 0) sEtiqueta = "THOUSAND ";
						sLetras = sLetras + sEtiqueta;
					}
					//Busca Millones
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(6, 9));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "ONE MILLION ";
						else {
							sEtiqueta = "MILLIONS ";
							iCen = Integer.parseInt(sImpCad.substring(6, 7));
							iDes = Integer.parseInt(sImpCad.substring(7, 8));
							iUni = Integer.parseInt(sImpCad.substring(8, 9));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					}
					//Busca miles
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(9, 12));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "ONE THOUSAND ";
						else {
							sEtiqueta = "THOUSAND ";
							iCen = Integer.parseInt(sImpCad.substring(9, 10));
							iDes = Integer.parseInt(sImpCad.substring(10, 11));
							iUni = Integer.parseInt(sImpCad.substring(11, 12));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					}
					//Busca Centenas
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(12, 15));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "ONE DOLLAR ";
						else {
							sEtiqueta = "DOLLAR ";
							iCen = Integer.parseInt(sImpCad.substring(12, 13));
							iDes = Integer.parseInt(sImpCad.substring(13, 14));
							iUni = Integer.parseInt(sImpCad.substring(14, 15));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					}
					if(iBillon == 0) {
						if(Integer.parseInt(sImpCad.substring(9, 12)) == 0)
							sLetras = sLetras + "OF DOLLARS ";
						else
							sLetras = sLetras + "DOLLARS ";
					}
					if(sDivisa.equals("DLS"))
						resp = sLetras + sImpCad.substring(13, 15) + "/100 USD";
					else
						resp = sLetras + sImpCad.substring(13, 15) + "/100 " + sDivisa;
				}else {
					sImpCad = funciones.ponerCeros(sImpCad, 18);
					iBillon = Integer.parseInt(sImpCad.substring(0, 3));
					
					if(iBillon > 0) {
						if(iBillon == 1)
							sEtiqueta = "UN BILLON ";
						else {
							sEtiqueta = "BILLONES ";
							iCen = Integer.parseInt(sImpCad.substring(0, 1));
							iDes = Integer.parseInt(sImpCad.substring(1, 2));
							iUni = Integer.parseInt(sImpCad.substring(2, 3));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					} 
					//Busca mil millones
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(3, 6));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "MIL MILLONES ";
						else {
							sEtiqueta = "BILLONES ";
							iCen = Integer.parseInt(sImpCad.substring(3, 4));
							iDes = Integer.parseInt(sImpCad.substring(4, 5));
							iUni = Integer.parseInt(sImpCad.substring(5, 6));
							calculaCentenasMN();
						}
						iBillon = Integer.parseInt(sImpCad.substring(6, 9));
						
						if(iBillon > 0) sEtiqueta = "MIL ";
						sLetras = sLetras + sEtiqueta;
					}
					//Busca Millones
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(6, 9));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "UN MILLON ";
						else {
							sEtiqueta = "MILLONES ";
							iCen = Integer.parseInt(sImpCad.substring(6, 7));
							iDes = Integer.parseInt(sImpCad.substring(7, 8));
							iUni = Integer.parseInt(sImpCad.substring(8, 9));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					}
					//Busca miles
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(9, 12));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "MIL ";
						else {
							sEtiqueta = "MIL ";
							iCen = Integer.parseInt(sImpCad.substring(9, 10));
							iDes = Integer.parseInt(sImpCad.substring(10, 11));
							iUni = Integer.parseInt(sImpCad.substring(11, 12));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					}
					//Busca Centenas
					sEtiqueta = " ";
					iBillon = Integer.parseInt(sImpCad.substring(12, 15));
					
					if(iBillon > 0) {
						if(iBillon == 1) sEtiqueta = "UN PESO ";
						else {
							sEtiqueta = "PESOS ";
							iCen = Integer.parseInt(sImpCad.substring(12, 13));
							iDes = Integer.parseInt(sImpCad.substring(13, 14));
							iUni = Integer.parseInt(sImpCad.substring(14, 15));
							calculaCentenasMN();
						}
						sLetras = sLetras + sEtiqueta;
					}
					if(iBillon == 0) {
						if(Integer.parseInt(sImpCad.substring(9, 12)) == 0)
							sLetras = sLetras + "DE PESOS";
						else
							sLetras = sLetras + "PESOS ";
					}
					resp = sLetras + sImpCad.substring(16, 18) + "/100 M.N.";
				}
			}
			}catch(Exception e) {
				bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:CapturaSolicitudesPagoAction, M:letrasCantidad");
			}
			return resp;
		}
		//no se ocupa en esta clase ni es mandado llamar desde el js "Captura solicitud de pago"
		public void calculaCentenasDolares(String sBillon) {
			List<String> wCentenas = new ArrayList<String>();
			
			try {
				if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
					wCentenas.add(0, " ");
					wCentenas.add(1, "ONE HUNDRED ");
					wCentenas.add(2, "TWO HUNDRED ");
					wCentenas.add(3, "THREE HUNDRED ");
					wCentenas.add(4, "FOUR HUNDRED ");
					wCentenas.add(5, "FIVE HUNDRED ");
					wCentenas.add(6, "SIX HUNDRED ");
					wCentenas.add(7, "SEVEN HUNDRED ");
					wCentenas.add(8, "EIGHT HUNDRED ");
					wCentenas.add(9, "NINE HUNDRED ");
					
					if(iCen > 0) {							//Si existen Centenas
						if(iCen == 1) {
							if(iDes == 0 && iUni == 0) {
								sLetras =  sLetras + wCentenas.get(iCen);
								return;
							}else
								sLetras =  sLetras + "CIENTO ";
						}else {
							sLetras = sLetras + wCentenas.get(iCen);
						}
					}
					if(iDes > 0) calculaDecenasDolares();		//Si existen Decenas
					if(iUni > 0) calculaUnidadesDolares();		//Si existen Unidades
				}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:CapturaSolicitudesPagoAction, M:calculaCentenasDolares");
		}
	}
	//no se ocupa en esta clase ni es mandado llamar desde el js "Captura solicitud de pago"
	public void calculaDecenasDolares() {
		List<String> wDecenas = new ArrayList<String>();
		
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				wDecenas.add(0, " ");
				wDecenas.add(1, "TEN ");
				wDecenas.add(2, "TWENTY ");
				wDecenas.add(3, "THIRTY ");
				wDecenas.add(4, "FORTY ");
				wDecenas.add(5, "FIFTY ");
				wDecenas.add(6, "SIXTY ");
				wDecenas.add(7, "SEVENTY ");
				wDecenas.add(8, "EIGHTY ");
				wDecenas.add(9, "NINETY ");
				
				if(iDes == 1){
					if(iUni == 0) sLetras = sLetras + "TEN ";
					if(iUni == 1) sLetras = sLetras + "ELEVEN ";
					if(iUni == 2) sLetras = sLetras + "TWELVE ";
					if(iUni == 3) sLetras = sLetras + "THIRTEEN ";
					if(iUni == 4) sLetras = sLetras + "FOURTEEN ";
					if(iUni == 5) sLetras = sLetras + "FIFTEEN ";
					if(iUni == 6) sLetras = sLetras + "SIXTEEN ";
					if(iUni == 7) sLetras = sLetras + "SEVENTEEN ";
					if(iUni == 8) sLetras = sLetras + "EIGHTTEEN ";
					if(iUni == 9) sLetras = sLetras + "NINETEEN ";
				}else {
					sLetras = sLetras + wDecenas.get(iDes);
					calculaUnidadesDolares();
				}
				if(iDes == 2) {
					if(iUni ==0)
						sLetras = sLetras + "TWENTY ";
					else {
						sLetras = sLetras + "TWENTY";
						calculaUnidadesDolares();
					}
				}
				if(iDes > 2) {
					sLetras = sLetras + wDecenas.get(iDes);
					
					if(iUni != 0) calculaUnidadesDolares();
				}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:CapturaSolicitudesPagoAction, M:calculaDecenasDolares");
		}
	}
	//no se ocupa en esta clase ni es mandado llamar desde el js "Captura solicitud de pago"
	public void calculaUnidadesDolares() {
		List<String> wUnidades = new ArrayList<String>();
		
		try {
			wUnidades.add(0, " ");
			wUnidades.add(1, "ONE ");
			wUnidades.add(2, "TWO ");
			wUnidades.add(3, "THREE ");
			wUnidades.add(4, "FOUR ");
			wUnidades.add(5, "FIVE ");
			wUnidades.add(6, "SIX ");
			wUnidades.add(7, "SEVEN ");
			wUnidades.add(8, "EIGHT ");
			wUnidades.add(9, "NINE ");
			
			if (iUni > 0) sLetras = sLetras + wUnidades.get(iUni);
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:CapturaSolicitudesPagoAction, M:calculaUnidadesDolares");
		}
	}
	
	//Comienzan los pesos
	public void calculaCentenasMN() {
		List<String> wCentenas = new ArrayList<String>();
		
		try {
			wCentenas.add(0, " ");
			wCentenas.add(1, "CIEN ");
			wCentenas.add(2, "DOSCIENTOS ");
			wCentenas.add(3, "TRESCIENTOS ");
			wCentenas.add(4, "CUATROCIENTOS ");
			wCentenas.add(5, "QUINIENTOS ");
			wCentenas.add(6, "SEISCIENTOS ");
			wCentenas.add(7, "SETECIENTOS ");
			wCentenas.add(8, "OCHOCIENTOS ");
			wCentenas.add(9, "NOVECIENTOS ");
			
			if(iCen > 0) {							//Si existen Centenas
				if(iCen == 1) {
					if(iDes == 0 && iUni == 0) {
						sLetras =  sLetras + wCentenas.get(iCen);
						return;
					}else
						sLetras =  sLetras + "CIENTO ";
				}else {
					sLetras = sLetras + wCentenas.get(iCen);
				}
			}
			if(iDes > 0) calculaDecenasMN();		//Si existen Decenas
			if(iUni > 0) calculaUnidadesMN();		//Si existen Unidades
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:CapturaSolicitudesPagoAction, M:calculaCentenasMN");
		}
	}
	
	public void calculaDecenasMN() {
		List<String> wDecenas = new ArrayList<String>();
		
		try {
			wDecenas.add(0, " ");
			wDecenas.add(1, "DIEZ ");
			wDecenas.add(2, "VEINTE ");
			wDecenas.add(3, "TREINTA ");
			wDecenas.add(4, "CUARENTA ");
			wDecenas.add(5, "CINCUENTA ");
			wDecenas.add(6, "SESENTA ");
			wDecenas.add(7, "SETENTA ");
			wDecenas.add(8, "OCHENTA ");
			wDecenas.add(9, "NOVENTA ");
			
			if(iDes == 1){
				if(iUni == 0) sLetras = sLetras + "DIEZ ";
				if(iUni == 1) sLetras = sLetras + "ONCE ";
				if(iUni == 2) sLetras = sLetras + "DOCE ";
				if(iUni == 3) sLetras = sLetras + "TRECE ";
				if(iUni == 4) sLetras = sLetras + "CATORCE ";
				if(iUni == 5) sLetras = sLetras + "QUINCE ";
				if(iUni > 5) {
					sLetras = sLetras + "DIECI";
					calculaUnidadesMN();
				}		
			}
			if(iDes == 2){
				if(iUni == 0)
					sLetras = sLetras + "VEINTE";
				else {
					sLetras = sLetras + "VEINTI";
					calculaUnidadesMN();
				}
			}
			if(iDes > 2){
				sLetras = sLetras + wDecenas.get(iDes);
				
				if(iUni != 0) {
					sLetras = sLetras + " Y ";
					calculaUnidadesMN();
				}
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:CapturaSolicitudesPagoAction, M:calculaDecenasMN");
		}
	}
	
	public void calculaUnidadesMN() {
		List<String> wUnidades = new ArrayList<String>();
		
		try {
			wUnidades.add(0, " ");
			wUnidades.add(1, "UN ");
			wUnidades.add(2, "DOS ");
			wUnidades.add(3, "TRES ");
			wUnidades.add(4, "CUATRO ");
			wUnidades.add(5, "CINCO ");
			wUnidades.add(6, "SEIS ");
			wUnidades.add(7, "SIETE ");
			wUnidades.add(8, "OCHO ");
			wUnidades.add(9, "NUEVE ");
			
			if (iUni > 0) sLetras = sLetras + wUnidades.get(iUni);
			
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:CapturaSolicitudesPagoAction, M:calculaUnidadesMN");
		}
	}
	PagosPedientesService pendientesService;
	@DirectMethod
	public String controlFechas(String fechaHoy, String origen){
		System.out.println(fechaHoy);
		String mensaje = "";
		
		try {
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				pendientesService = (PagosPedientesService)contexto.obtenerBean("pagosPedientesBusinessImpl");
				mensaje = pendientesService.controlFechas(fechaHoy, origen);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:ConsultaPropuestasAction, M:controlFechas");
		}
		
		return mensaje;
	}
	@DirectMethod
	public Map<String, Object> obtieneCheqPagadora(int idEmpresa,String idDivOriginal,int idFormaPago){
	
		Map<String, Object> resultado = new HashMap<>();
		resultado.put("idChequera", "");
		resultado.put("idBanco", "");
		Gson gson = new Gson();
		//List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		  List<ParametroDto> datos=new ArrayList<ParametroDto>();
		
		try {
			System.out.println("datooo "+idEmpresa+" "+idDivOriginal+" "+idFormaPago);
			if(Utilerias.haveSession(WebContextManager.get())&& Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
			 	resultado = capturaSolicitudesPagoService.obtieneCheqPagadora(idEmpresa,idDivOriginal,idFormaPago);
			 	System.out.println("bancoo "+resultado.get("id_banco"));
			 	if(!resultado.isEmpty()){
			 		System.out.println("Entro a asignar datos ");
			 		resultado.put("idChequera", resultado.get("id_chequera"));
					resultado.put("idBanco", resultado.get("id_banco"));
			 		
			 	}
			 //	System.out.println("id_banco "+((ParametroDto) datos).getIdBanco());
			 	return resultado;
			 	
			}	 	
		} catch (Exception e) {
			e.printStackTrace();
			//resultado.put("msgUsuario", "Datos incompletos");
//			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
//			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:generarDocumento");
		}
		return resultado;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto> obtenerGrupo(){
		List<LlenaComboGralDto> result = new ArrayList<LlenaComboGralDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				result = capturaSolicitudesPagoService.obtenerGrupo();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:obtenerGrupoDelRubro");			
		}
		return result;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>obtenerRubro(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		System.out.println("condicion "+condicion);
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				CapturaSolicitudesPagoService capturaSolicitudesPagoService = (CapturaSolicitudesPagoService)contexto.obtenerBean("capturaSolicitudesPagoBusinessImpl");
				dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
				dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
				dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
				dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
				dto.setOrden(orden!=null && !orden.equals("")?orden:"");
				list=capturaSolicitudesPagoService.obtenerRubro(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:llenaCombo");	
		}
		return list;
	}
}
