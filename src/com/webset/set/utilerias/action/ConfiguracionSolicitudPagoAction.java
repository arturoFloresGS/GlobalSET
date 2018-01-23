package com.webset.set.utilerias.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.service.ConfiguracionSolicitudPagoService;
import com.webset.set.utileriasmod.dto.ConfiguracionSolicitudPagoDto;
import com.webset.utils.tools.Utilerias;

public class ConfiguracionSolicitudPagoAction {
	Contexto contexto = new Contexto();
	Bitacora bitacora = new Bitacora();
	ConfiguracionSolicitudPagoService objConfiguracionSolicitudPagoService;
	
	
	@DirectMethod
	public List<ConfiguracionSolicitudPagoDto> llenaGridBeneficiarios(String idPoliza, String idGrupo, String idRubro,String beneficiarios){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return listaResultado;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			listaResultado = objConfiguracionSolicitudPagoService.llenaGridBeneficiarios(idPoliza,idGrupo,idRubro,beneficiarios);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: llenaGridBeneficiarios");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<ConfiguracionSolicitudPagoDto> llenaGridPolizas(){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return listaResultado;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			listaResultado = objConfiguracionSolicitudPagoService.llenaGridPolizas();
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: llenaGridPolizas");
		} return listaResultado;
	}
		
	@DirectMethod
	public List<ConfiguracionSolicitudPagoDto> llenaComboPoliza(){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return listaResultado;
		try{
			String numUsuario =GlobalSingleton.getInstancia().getUsuarioLoginDto().getIdUsuario()+"";
			if(numUsuario.equals(""))
				return null;
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			listaResultado = objConfiguracionSolicitudPagoService.llenaComboPoliza(numUsuario);
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: llenaComboPoliza");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<ConfiguracionSolicitudPagoDto> llenaComboRubro(String idPoliza){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return listaResultado;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			listaResultado = objConfiguracionSolicitudPagoService.llenaComboRubro(idPoliza+"");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: llenaComboRubro");
		} return listaResultado;
	}
	
	@DirectMethod
	public List<ConfiguracionSolicitudPagoDto> llenaComboGrupo(String idRubro){
		List<ConfiguracionSolicitudPagoDto> listaResultado = new ArrayList<ConfiguracionSolicitudPagoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return listaResultado;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			listaResultado = objConfiguracionSolicitudPagoService.llenaComboGrupo(idRubro+"");
		}
		catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: llenaComboGrupo");
		} return listaResultado;
	}
	
	@DirectMethod
	public String existeConfiguracionSolicitudPago(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return resultado;
		Gson gson = new Gson();
		ConfiguracionSolicitudPagoDto dto=new ConfiguracionSolicitudPagoDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPoliza(objDatos.get(t).get("idPoliza")!=null ? objDatos.get(t).get("idPoliza"):"");
				dto.setIdGrupo(objDatos.get(t).get("idGrupo")!=null ? objDatos.get(t).get("idGrupo"):"" );
				dto.setIdRubro(objDatos.get(t).get("idRubro")!=null ? objDatos.get(t).get("idRubro"):"" );
				
			}
			resultado = objConfiguracionSolicitudPagoService.existeConfiguracionSolicitudPago(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: existeConfiguracionSolicitudPago");
		} return resultado;	
	}
	
	@DirectMethod
	public ConfiguracionSolicitudPagoDto consultarConfiguracionSolicitudPago(String sJson){
		ConfiguracionSolicitudPagoDto dto=new ConfiguracionSolicitudPagoDto();
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return dto;
		Gson gson = new Gson();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPoliza(objDatos.get(t).get("idPoliza")!=null ? objDatos.get(t).get("idPoliza"):"");
				dto.setIdGrupo(objDatos.get(t).get("idGrupo")!=null ? objDatos.get(t).get("idGrupo"):"" );
				dto.setIdRubro(objDatos.get(t).get("idRubro")!=null ? objDatos.get(t).get("idRubro"):"" );	
			}
			dto = objConfiguracionSolicitudPagoService.consultarConfiguracionSolicitudPago(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: consultarConfiguracionSolicitudPago");
		} return dto;	
	}
	
	@DirectMethod
	public String insertaConfiguracionSolicitudPago(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return resultado;
		Gson gson = new Gson();
		ConfiguracionSolicitudPagoDto dto=new ConfiguracionSolicitudPagoDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;	
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPoliza(objDatos.get(t).get("idPoliza")!=null ? objDatos.get(t).get("idPoliza"):"");
				dto.setIdGrupo(objDatos.get(t).get("idGrupo")!=null ? objDatos.get(t).get("idGrupo"):"" );
				dto.setIdRubro(objDatos.get(t).get("idRubro")!=null ? objDatos.get(t).get("idRubro"):"" );
				dto.setTexto(objDatos.get(t).get("texto")!=null ? objDatos.get(t).get("texto"):"" );
				dto.setTextoCabecera(objDatos.get(t).get("textoCabecera")!=null ? objDatos.get(t).get("textoCabecera"):"" );
				dto.setAsignacion(objDatos.get(t).get("asignacion")!=null ? objDatos.get(t).get("asignacion"):"" );
				dto.setReferencia(objDatos.get(t).get("referenciaPago")!=null ? objDatos.get(t).get("referenciaPago"):"" );
				dto.setReferenciaPago(objDatos.get(t).get("referencia")!=null ? objDatos.get(t).get("referencia"):"" );
				dto.setCentroCostos(objDatos.get(t).get("centroCostos")!=null ? objDatos.get(t).get("centroCostos"):"" );
				dto.setDivision(objDatos.get(t).get("division")!=null ? objDatos.get(t).get("division"):"" );
				dto.setOrden(objDatos.get(t).get("orden")!=null ? objDatos.get(t).get("orden"):"" );
				dto.setFechaPago(objDatos.get(t).get("fechaPago")!=null ? objDatos.get(t).get("fechaPago"):"" );
				dto.setObservaciones(objDatos.get(t).get("observaciones")!=null ? objDatos.get(t).get("observaciones"):"" );
				dto.setDivisaPago(objDatos.get(t).get("divisaPago")!=null ? objDatos.get(t).get("divisaPago"):"" );
				dto.setAreaDestino(objDatos.get(t).get("areaDestino")!=null ? objDatos.get(t).get("areaDestino"):"" );
				dto.setClaseDoc(objDatos.get(t).get("claseDoc")!=null ? objDatos.get(t).get("claseDoc"):"" );
				dto.setTransaccion(objDatos.get(t).get("transaccion")!=null ? objDatos.get(t).get("transaccion"):"" );
				dto.setPagoSinPoliza(objDatos.get(t).get("pagoSinPoliza")!=null ? objDatos.get(t).get("pagoSinPoliza"):"" );
				dto.setSociedadGL(objDatos.get(t).get("sociedadGL")!=null ? objDatos.get(t).get("sociedadGL"):"" );
				dto.setChequeCaja(objDatos.get(t).get("chequeCaja")!=null ? objDatos.get(t).get("chequeCaja"):"" );
				dto.setCheque(objDatos.get(t).get("cheque")!=null ? objDatos.get(t).get("cheque"):"" );
				dto.setTransferencia(objDatos.get(t).get("transferencia")!=null ? objDatos.get(t).get("transferencia"):"" );
				dto.setCargoCuenta(objDatos.get(t).get("cargoCuenta")!=null ? objDatos.get(t).get("cargoCuenta"):"" );
				dto.setBancoInterlocutor(objDatos.get(t).get("bancoInterlocutor")!=null ? objDatos.get(t).get("bancoInterlocutor"):"" );
			}
			resultado = objConfiguracionSolicitudPagoService.insertaConfiguracionSolicitudPago(dto);
		}catch(Exception e){
			System.out.println("error action");
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: llenaGridSolicitantesFirmantes");
		} return resultado;	
	}
		
	@DirectMethod
	public String updateConfiguracionSolicitudPago(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return resultado;
		Gson gson = new Gson();
		ConfiguracionSolicitudPagoDto dto=new ConfiguracionSolicitudPagoDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPoliza(objDatos.get(t).get("idPoliza")!=null ? objDatos.get(t).get("idPoliza"):"");
				dto.setIdGrupo(objDatos.get(t).get("idGrupo")!=null ? objDatos.get(t).get("idGrupo"):"" );
				dto.setIdRubro(objDatos.get(t).get("idRubro")!=null ? objDatos.get(t).get("idRubro"):"" );
				dto.setTexto(objDatos.get(t).get("texto")!=null ? objDatos.get(t).get("texto"):"" );
				dto.setTextoCabecera(objDatos.get(t).get("textoCabecera")!=null ? objDatos.get(t).get("textoCabecera"):"" );
				dto.setAsignacion(objDatos.get(t).get("asignacion")!=null ? objDatos.get(t).get("asignacion"):"" );
				dto.setReferencia(objDatos.get(t).get("referenciaPago")!=null ? objDatos.get(t).get("referenciaPago"):"" );
				dto.setReferenciaPago(objDatos.get(t).get("referencia")!=null ? objDatos.get(t).get("referencia"):"" );
				dto.setCentroCostos(objDatos.get(t).get("centroCostos")!=null ? objDatos.get(t).get("centroCostos"):"" );
				dto.setDivision(objDatos.get(t).get("division")!=null ? objDatos.get(t).get("division"):"" );
				dto.setOrden(objDatos.get(t).get("orden")!=null ? objDatos.get(t).get("orden"):"" );
				dto.setFechaPago(objDatos.get(t).get("fechaPago")!=null ? objDatos.get(t).get("fechaPago"):"" );
				dto.setObservaciones(objDatos.get(t).get("observaciones")!=null ? objDatos.get(t).get("observaciones"):"" );
				dto.setDivisaPago(objDatos.get(t).get("divisaPago")!=null ? objDatos.get(t).get("divisaPago"):"" );
				dto.setAreaDestino(objDatos.get(t).get("areaDestino")!=null ? objDatos.get(t).get("areaDestino"):"" );
				dto.setClaseDoc(objDatos.get(t).get("claseDoc")!=null ? objDatos.get(t).get("claseDoc"):"" );
				dto.setTransaccion(objDatos.get(t).get("transaccion")!=null ? objDatos.get(t).get("transaccion"):"" );
				dto.setPagoSinPoliza(objDatos.get(t).get("pagoSinPoliza")!=null ? objDatos.get(t).get("pagoSinPoliza"):"" );
				dto.setSociedadGL(objDatos.get(t).get("sociedadGL")!=null ? objDatos.get(t).get("sociedadGL"):"" );
				dto.setChequeCaja(objDatos.get(t).get("chequeCaja")!=null ? objDatos.get(t).get("chequeCaja"):"" );
				dto.setCheque(objDatos.get(t).get("cheque")!=null ? objDatos.get(t).get("cheque"):"" );
				dto.setTransferencia(objDatos.get(t).get("transferencia")!=null ? objDatos.get(t).get("transferencia"):"" );
				dto.setCargoCuenta(objDatos.get(t).get("cargoCuenta")!=null ? objDatos.get(t).get("cargoCuenta"):"" );
				dto.setBancoInterlocutor(objDatos.get(t).get("bancoInterlocutor")!=null ? objDatos.get(t).get("bancoInterlocutor"):"" );
							}
			resultado = objConfiguracionSolicitudPagoService.updateConfiguracionSolicitudPago(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: updateConfiguracionSolicitudPago");
		} return resultado;	
	}
		
	@DirectMethod
	public String deleteConfiguracionSolicitudPago(String sJson){
		String resultado="";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return resultado;
		Gson gson = new Gson();
		ConfiguracionSolicitudPagoDto dto=new ConfiguracionSolicitudPagoDto();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		int t;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				dto.setIdPoliza(objDatos.get(t).get("idPoliza")!=null ? objDatos.get(t).get("idPoliza"):"");
				dto.setIdGrupo(objDatos.get(t).get("idGrupo")!=null ? objDatos.get(t).get("idGrupo"):"" );
				dto.setIdRubro(objDatos.get(t).get("idRubro")!=null ? objDatos.get(t).get("idRubro"):"" );
				
			}
			resultado = objConfiguracionSolicitudPagoService.deleteConfiguracionSolicitudPago(dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M: deleteConfiguracionSolicitudPago");
		} return resultado;	
	}
	
	//Beneficiarios para la configuracion de solicitud Pago
	@DirectMethod
	public String existenBeneficiarios(String sJson){
		String resultado="Error al cargar archivo";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return resultado;
		Gson gson = new Gson();
		List<Map<String, String>> objDatos = gson.fromJson(sJson, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String beneficiarios[] = new String[objDatos.size()];
		int t;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			for(t=0; t<objDatos.size(); t++) {
				beneficiarios[t]=""+objDatos.get(t).get("beneficiario");
			}
			resultado = objConfiguracionSolicitudPagoService.existenBeneficiarios(beneficiarios);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M:existenBeneficiarios");
		} return resultado;	
	}
	
	@DirectMethod
	public String guardarBeneficiarios(String sJsonB,String sJsonC){
		String resultado="Error al cargar archivo";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return resultado;
		Gson gson = new Gson();
		List<Map<String, String>> objDatosB = gson.fromJson(sJsonB, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> objDatosC = gson.fromJson(sJsonC, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		String beneficiarios[] = new String[objDatosB.size()];
		ConfiguracionSolicitudPagoDto dto=new ConfiguracionSolicitudPagoDto();
		int t;
		try{
			objConfiguracionSolicitudPagoService = (ConfiguracionSolicitudPagoService)contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			for(t=0; t<objDatosB.size(); t++) {
				beneficiarios[t]=""+objDatosB.get(t).get("beneficiario");
			}
			for(t=0; t<objDatosC.size(); t++) {
				dto.setIdPoliza(objDatosC.get(t).get("idPoliza")!=null ? objDatosC.get(t).get("idPoliza"):"");
				dto.setIdGrupo(objDatosC.get(t).get("idGrupo")!=null ? objDatosC.get(t).get("idGrupo"):"" );
				dto.setIdRubro(objDatosC.get(t).get("idRubro")!=null ? objDatosC.get(t).get("idRubro"):"" );	
			}
			resultado = objConfiguracionSolicitudPagoService.guardarBeneficiarios(beneficiarios,dto);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Utilerias, C:ConfiguracionSolicitudPagoAction, M:guardarBeneficiarios");
		} return resultado;	
	}
	
	/***********EXCEL********/
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
			+"P:Utilerias, C:ConfiguracionSolicitudPagoAction, M:obtenerExcel");
		} catch (IOException e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ConfiguracionSolicitudPagoAction, M:obtenerExcel");
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Utilerias, C:ConfiguracionSolicitudPagoAction, M:obtenerExcel");
		}
		arch.delete();
		return workbook;
	}
	
	@DirectMethod
	public String exportaExcel(String datos) {
		String resultado = "";
		if(!Utilerias.haveSession(WebContextManager.get()) || !Utilerias.tienePermiso(WebContextManager.get(), 198))
			return resultado;
		try {
			objConfiguracionSolicitudPagoService  = (ConfiguracionSolicitudPagoService )contexto.obtenerBean("objConfiguracionSolicitudPagoBusinessImpl");
			resultado = objConfiguracionSolicitudPagoService .exportaExcel(datos);
		}catch(Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P: Egresos, C:ConfiguracionSolicitudPagoAction, M: exportaExcel");
		}
		return resultado;
	}	
}
