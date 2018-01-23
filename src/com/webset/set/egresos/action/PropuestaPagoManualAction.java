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
import com.webset.set.egresos.dto.PropuestaPagoManualDto;
import com.webset.set.egresos.service.PropuestaPagoManualService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.EstatusMovimientosDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.utils.tools.Utilerias;
/**
 * 
 * @author Jessica Arelly Cruz Cruz
 * @since 14/02/2011
 *
 */

public class PropuestaPagoManualAction {
	String sLetras = " ";
	int iCen = 0;
	int iDes = 0;
	int iUni = 0;
	private Contexto contexto = new Contexto();
	private Bitacora bitacora = new Bitacora();
	private PropuestaPagoManualService propuestaPagoManualService;
	private Funciones funciones;
	
	@DirectMethod
	public String obtenerFechaManana(){
		String fechaString="";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			fechaString= propuestaPagoManualService.obtenerFechaManana().toString();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:obtenerFechaManana");			
		}
		return fechaString;
	}
	
	@DirectMethod
	public String obtenerFechaHoy(){
		String fechaString="";
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			fechaString= propuestaPagoManualService.obtenerFechaHoy().toString();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:obtenerFechaHoy");			
		}
		return fechaString;
	}
	
	@DirectMethod
	public List<LlenaComboDivisaDto> llenarComboDivisa(String psDivisa){
		List<LlenaComboDivisaDto>list=new ArrayList<LlenaComboDivisaDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			list=propuestaPagoManualService.llenarComboDivisa(psDivisa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboDivisa");			
		}
		return list;
	}
	
	@DirectMethod
	public boolean controlProyecto(int egreso, int bloqueo) {
		String res = "";
		boolean result = false;
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			res = propuestaPagoManualService.controlProyecto(egreso, bloqueo);
			if(res.equals("S"))
				result = true;
			else
				result = false;
			}
		} catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Egresos, C:PropuestaPagoManualAction, M:controlProyecto");
		}
		return result;
	}
	
	/**
	 * se llena el combo grupo con el metodo de llena combo general
	 * @param noUsuario
	 * @param pagoEmpresa
	 * @param noEmpresa
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboGrupo(int noUsuario,int pagoEmpresa, int noEmpresa){
		Map<String,Object>datos= new HashMap<String,Object>();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			datos.put("noUsuario",noUsuario);
			datos.put("pagoEmpresa",pagoEmpresa);
			datos.put("noEmpresa",noEmpresa);
			list=propuestaPagoManualService.llenarComboGrupo(datos);
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboGrupo");
		}
		return list;
	}
	
	/**
	 * llenado del combo banco de la ventana de actualizar chequeras
	 * @param idDivisa
	 * @param noEmpresa
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboBanco(String idDivisa, int noEmpresa){
		Map<String,Object>datos= new HashMap<String,Object>();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			datos.put("idDivisa",idDivisa);
			datos.put("noEmpresa",noEmpresa);
			list=propuestaPagoManualService.llenarComboBanco(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboBanco");
		}
		return list;
	}
	
	@DirectMethod
	public List<LlenaComboGralDto>llenarComboChequera(String idDivisa, int noEmpresa, int idBanco, String psDivision, String Persona, String piNoEmpresa){
		Map<String,Object>datos= new HashMap<String,Object>();
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			datos.put("idDivisa",idDivisa);
			datos.put("noEmpresa",noEmpresa);
			datos.put("idBanco", idBanco);
			datos.put("psDivisa", psDivision);
			datos.put("Persona", Persona);
			datos.put("piNoEmpresa", piNoEmpresa);
			list=propuestaPagoManualService.llenarComboChequera(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboChequera");
		}
		return list;
	}
	
	/**
	 * llenar combo general
	 * @param campoUno
	 * @param campoDos
	 * @param tabla
	 * @param condicion
	 * @param orden
	 * @return
	 */
	@DirectMethod
	public List<LlenaComboGralDto>llenarCombo(String campoUno, String campoDos, String tabla, String condicion, String orden){
		List<LlenaComboGralDto> list= new ArrayList<LlenaComboGralDto>();
		LlenaComboGralDto dto= new LlenaComboGralDto();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			dto.setCampoUno(campoUno!=null && !campoUno.equals("")?campoUno:"");
			dto.setCampoDos(campoDos!=null && !campoDos.equals("")?campoDos:"");
			dto.setTabla(tabla!=null && !tabla.equals("")?tabla:"");
			dto.setCondicion(condicion!=null && !condicion.equals("")?condicion:"");
			dto.setOrden(orden!=null && !orden.equals("")?orden:"");
			list=propuestaPagoManualService.llenarComboGral(dto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarCombo");	
		}
		return list;
	}

	/**
	 * consulta de propuestas
	 * @param data
	 * @return lista
	 */
	@DirectMethod
	public List<PropuestaPagoManualDto> consultarPropuestas(String data){
		Map<String, String> datos=new HashMap<String, String>();
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<PropuestaPagoManualDto> list= new ArrayList<PropuestaPagoManualDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			datos.put("grupoEmpresa", objParams.get(0).get("grupo"));//grupo de empresas
			datos.put("noEmpresa", objParams.get(0).get("empresa"));
			datos.put("usuario", objParams.get(0).get("usuario"));
			datos.put("tipoChequeNE", objParams.get(0).get("tipoChequeNE"));
			datos.put("psDivision", objParams.get(0).get("psDivision"));
			datos.put("bloqueo", objParams.get(0).get("bloqueo"));
			datos.put("divisa", objParams.get(0).get("divisa"));
			datos.put("formaPago", objParams.get(0).get("formaPago"));
			datos.put("division", "");
			//criterios que por el momento son nulos
			datos.put("estatus", objParams.get(0).get("estatus"));
			datos.put("fecha1", null);
			datos.put("fecha2", null);
			datos.put("bancoReceptor", null);
			datos.put("concepto", objParams.get(0).get("concepto"));
			datos.put("loteEntrada", null);
			datos.put("caja", null);
			datos.put("bancoPagador", null);
			datos.put("claveOperacion", objParams.get(0).get("cveOperacion"));
			datos.put("noProveedor1", null);
			datos.put("noProveedor2", null);
			datos.put("factura", objParams.get(0).get("factura"));
			datos.put("noPedido", objParams.get(0).get("noPedido"));
			datos.put("origenMovimiento", null);
			datos.put("rubroMovimiento", null);
			datos.put("chequeraBenef", null);
			datos.put("chequeraPagadora", null);
			datos.put("noDocumentoIni", null);
			datos.put("noDocumentoFin", null);
			datos.put("claseDocumento", null);
			datos.put("monto1", objParams.get(0).get("monto1"));
			datos.put("monto2", objParams.get(0).get("monto2"));
			datos.put("nombreProveedor", null);
			
			list=propuestaPagoManualService.consultarPropuestas(datos);
			}
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:consultarPropuestas");	
		}
		return list;
	}
	
	
	/**
	 * consulta de propuestas
	 * @param data
	 * @return lista
	 */
	@DirectMethod
	public double sumarImportePropuestas(String data){
		Map<String, String> datos=new HashMap<String, String>();
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		double totalImporte = 0;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			datos.put("grupoEmpresa", objParams.get(0).get("grupo"));//grupo de empresas
			datos.put("noEmpresa", objParams.get(0).get("empresa"));
			datos.put("usuario", objParams.get(0).get("usuario"));
			datos.put("tipoChequeNE", objParams.get(0).get("tipoChequeNE"));
			datos.put("psDivision", objParams.get(0).get("psDivision"));
			datos.put("bloqueo", objParams.get(0).get("bloqueo"));
			datos.put("divisa", objParams.get(0).get("divisa"));
			datos.put("formaPago", objParams.get(0).get("formaPago"));
			datos.put("division", "");
			//criterios que por el momento son nulos
			datos.put("estatus", objParams.get(0).get("estatus"));
			datos.put("fecha1", null);
			datos.put("fecha2", null);
			datos.put("bancoReceptor", null);
			datos.put("concepto", objParams.get(0).get("concepto"));
			datos.put("loteEntrada", null);
			datos.put("caja", null);
			datos.put("bancoPagador", null);
			datos.put("claveOperacion", objParams.get(0).get("cveOperacion"));
			datos.put("noProveedor1", null);
			datos.put("noProveedor2", null);
			datos.put("factura", objParams.get(0).get("factura"));
			datos.put("noPedido", objParams.get(0).get("noPedido"));
			datos.put("origenMovimiento", null);
			datos.put("rubroMovimiento", null);
			datos.put("chequeraBenef", null);
			datos.put("chequeraPagadora", null);
			datos.put("noDocumentoIni", null);
			datos.put("noDocumentoFin", null);
			datos.put("claseDocumento", null);
			datos.put("monto1", objParams.get(0).get("monto1"));
			datos.put("monto2", objParams.get(0).get("monto2"));
			datos.put("nombreProveedor", null);
			
			totalImporte=propuestaPagoManualService.sumarImportePropuestas(datos);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:sumarImportePropuestas");	
		}
		return totalImporte;
	}
	
	
	/**
	 * se envian los parametros requeridos para generar la propuesta
	 * @param data
	 * @return
	 */
	@DirectMethod
	public Map<String, Object> ejecutarPropuestas(String data){
		Gson gson = new Gson();
		funciones = new Funciones();
		List<PropuestaPagoManualDto> listDto = new ArrayList<PropuestaPagoManualDto>();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String,Object> result= new HashMap<String,Object>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			
			for(int i = 0; i<objParams.size(); i++)
			{
				PropuestaPagoManualDto dto = new PropuestaPagoManualDto();
				dto.setFecPropuesta(funciones.ponerFechaDate(objParams.get(i).get("fecPropuesta")));
				dto.setFecPago(funciones.ponerFechaDate(objParams.get(i).get("fecPago")));
				dto.setNoFolioDet(objParams.get(i).get("folio"));
				dto.setImporte(Double.parseDouble(objParams.get(i).get("importe")));
				dto.setIdCaja(Integer.parseInt(objParams.get(i).get("idCaja")));
				dto.setNoDocto(objParams.get(i).get("noDocto"));
				dto.setIdFormaPago(Integer.parseInt(objParams.get(i).get("formaPago")));
				dto.setTotalPago(Double.parseDouble(objParams.get(i).get("totalPago")));
				dto.setIdBancoBenef(Integer.parseInt(objParams.get(i).get("giBanco")));
				dto.setDescBanco(objParams.get(i).get("gsDescBanco"));
				dto.setIdChequeraBenef(objParams.get(i).get("gsChequera"));
				dto.setNoUsuario(Integer.parseInt(objParams.get(i).get("usuario")));
				dto.setMontoMaximo(Double.parseDouble(objParams.get(i).get("montoMax")));
				dto.setImporteCupo(Double.parseDouble(objParams.get(i).get("importeCupo")));
				dto.setIdGrupoFlujo(Integer.parseInt(objParams.get(i).get("grupoFlujo")));
				dto.setIdDivisa(objParams.get(i).get("divisa"));
				dto.setIdDivisaPago(objParams.get(i).get("divisaPago"));
				dto.setTipoCambio(Double.parseDouble(objParams.get(i).get("tipoCambio")));
				dto.setGSIDCAJA(Integer.parseInt(objParams.get(i).get("giIdCaja")));
				dto.setCmbPropuesta(objParams.get(i).get("cmbPropuesta"));
				listDto.add(dto);
			}
			result=propuestaPagoManualService.ejecutarPropuestas(listDto);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:ejecutarPropuestas");	
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * funSQLVerificaEstatusMovtos
	 * @param psFolios
	 * @return
	 */
	@DirectMethod
	public boolean verificarEstatusMovtos(String psFolios){
		List<EstatusMovimientosDto> estatus = new ArrayList<EstatusMovimientosDto>();
		boolean res = false;
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			estatus = propuestaPagoManualService.verificarEstatusMovtos(psFolios);
			if (estatus.get(0).getMovMarcados() == estatus.get(0).getMovCorrectos())
			{
				res = true;
			}
			else res = false;
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:verificarEstatusMovtos");			
		}
		return res;
	}
	
	@DirectMethod
	public String ConfiguraSet(int param, String cliente) {
		String sino = ""; 
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
		 sino = propuestaPagoManualService.ConfiguraSet(param,cliente);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualAction, M:ConfiguraSet");
		}
		return sino;		
	}	
		
	@DirectMethod
	public String CompraTransf(int param) {
		String msj = " 1";
		//System.out.println("si entre ");
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			//propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			//ConfirmacionCargoCtaService confirmacionCargoCtaService = (ConfirmacionCargoCtaService)contexto.obtenerBean("confirmacionCargoCtaBusinessImpl");
			//num = confirmacionCargoCtaService.BancoPagador(noEmpresa, Divisa);    
			
			if(param == 0){
				msj = "La empresa no tiene chequeras para la divisa, por favor \n seleccione una divisa alterna de pago"; 
			}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:BancoPagador");
		}
		return msj;
	}  
	
	@DirectMethod
	public List<PropuestaPagoManualDto> obtenerDivisaAct(int empresa, String division) {
		List<PropuestaPagoManualDto> list = new ArrayList<PropuestaPagoManualDto>();   
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			list = propuestaPagoManualService.obtenerDivisaAct(empresa,division);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:ConfirmacionCargoCtaAction, M:obtenerDivisaAct");
		}
		return list;
	}  
	
	
	@DirectMethod
	public String muestraComponentes(int param) {
		String msj = ""; 
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			msj = propuestaPagoManualService.muestraComponentes(param); 
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:obtenerDivision");
		}
		return msj;
	}
	
	@DirectMethod
	public String CambiaChequerasMonedaPago(int banco, List<PropuestaPagoManualDto> datos, int registros){
		String res = "";
		int i = 0;
		List<PropuestaPagoManualDto> Datos = new ArrayList<PropuestaPagoManualDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			for(i = 0; i<registros; i++){
				if(datos.get(i).getDescFormaPago() == "TRANSFERENCIA"){
					int lAfectados = 0;
	                int iIdBancoBenef = 0;
	                String sIdChequeraBenef = "";
					Datos = propuestaPagoManualService.SelectBancoCheqBenef(datos.get(i).getNoCliente(), datos.get(i).getIdDivisa(), banco);
					
					if(Datos.size() > 0){
						iIdBancoBenef =	Datos.get(0).getIdBancoBenef();
						sIdChequeraBenef = Datos.get(0).getIdChequera();
						lAfectados = propuestaPagoManualService.UpdateBancoCheqBenef(datos.get(i).getNoFolioDet(), iIdBancoBenef, sIdChequeraBenef);
						if(lAfectados == 0)
							res = "No se pudo cambiar el Banco y Chequera Beneficiarios ";
					}
				}				
			}
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Egresos, C:PropuestaPagoManualAction, M:CambiaChequerasMonedaPago");
		}	
		return res;
	}
	@DirectMethod
	public List<PropuestaPagoManualDto>obtenerDivision(int usuario){
		List<PropuestaPagoManualDto> dto = new ArrayList<PropuestaPagoManualDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			
			dto = propuestaPagoManualService.obtenerDivision(usuario);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboGrupo");
		}
		return dto;
	}
	
	@DirectMethod
	public List<PropuestaPagoManualDto>consultarPropuestasAgregar(int noGrupoEmpresa) {
		List<PropuestaPagoManualDto> dto = new ArrayList<PropuestaPagoManualDto>();
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			
			dto = propuestaPagoManualService.consultarPropuestasAgregar(noGrupoEmpresa);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:PropuestaPagoManualAction, M:llenarComboGrupo");
		}
		return dto;
	}
	
	@DirectMethod
	public String agregarPropuestas(int noGrupoEmpresa, String cveControl, String sFolios) {
		String resp = "";
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			resp = propuestaPagoManualService.agregarPropuestas(noGrupoEmpresa, cveControl, sFolios);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) +"P:Egresos, C:PropuestaPagoManualAction, M:agregarPropuestas");
		}
		return resp;
	}
	
	@DirectMethod
	public Map<String, Object> ejecutarSolicitud(String data, String grid){
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		List<Map<String, String>> objGrid = gson.fromJson(grid, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		Map<String, Object> ret = new HashMap<String,Object>();
		
		try{
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			
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
		 	if(objGrid.get(0).get("area") == null) {
		 		ret.put("msgUsuario", "Ingrese el area o locaci�n");
				return ret;
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
//		 	for(int i = 0; i<objGrid.size(); i++)
//				logger.info("importe action " + i + " : " + objGrid.get(i).get("importe").replace(",", ""));
				
			ret = propuestaPagoManualService.ejecutarSolicitud(objParams.get(0),objGrid);
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Egresos, C:CapturaSolicitudesPagoAction, M:ejecutarSolicitud");	
		}
		return ret;
	}
	
	@DirectMethod
	public List<PropuestaPagoManualDto> llenaComboGrupoConta(String idTipoMovto, String idSubGrupo){
		List<PropuestaPagoManualDto>list = new ArrayList<PropuestaPagoManualDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			list=propuestaPagoManualService.llenaComboGrupoConta(idTipoMovto, idSubGrupo);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:llenaComboGrupoConta");			
		}
		return list;
	}
	
	@DirectMethod
	public List<PropuestaPagoManualDto> llenaComboSubGrupo(int tipoGrupo){
		List<PropuestaPagoManualDto>list = new ArrayList<PropuestaPagoManualDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			list=propuestaPagoManualService.llenaComboSubGrupo(tipoGrupo);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:llenaComboSubGrupo");			
		}
		return list;
	}
	
	@DirectMethod
	public List<PropuestaPagoManualDto> llenaComboSubSubGrupo(int idRubroC) {
		List<PropuestaPagoManualDto>list = new ArrayList<PropuestaPagoManualDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			list=propuestaPagoManualService.llenaComboSubSubGrupo(idRubroC);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:llenaComboSubSubGrupo");			
		}
		return list;
	}
	
	@DirectMethod
	public String buscaCtaContable(String datosConta) {
		String resp = "";
		Gson gson = new Gson();
		List<Map<String, String>> datos = gson.fromJson(datosConta, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			if(datos.get(0).get("idGrupo").toString().equals("") || datos.get(0).get("idRubro").toString().equals(""))
				return "Debe seleccionar grupo y rubro para continuar!!";
			
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			resp = propuestaPagoManualService.buscaCtaContable(datosConta);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:buscaCtaContable");			
		}
		return resp;
	}
	
	@DirectMethod
	public List<PropuestaPagoManualDto> llenarComboDepCCProy(int noEmpresa, int tipoCombo) {
		List<PropuestaPagoManualDto>list = new ArrayList<PropuestaPagoManualDto>();
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			list = propuestaPagoManualService.llenarComboDepCCProy(noEmpresa, tipoCombo);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:llenaComboSubSubGrupo");			
		}
		return list;
	}
	
	@DirectMethod
	public String modificaImporteApagar(int noFolioDet, double importe) {
		String resp = "";
		
		try {
			if (Utilerias.haveSession(WebContextManager.get())) {
			propuestaPagoManualService = (PropuestaPagoManualService) contexto.obtenerBean("propuestaPagoManualBusinessImpl");
			resp = propuestaPagoManualService.modificaImporteApagar(noFolioDet, importe);
			}
		}catch(Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e) + "P:Egresos, C:PropuestaPagoManualAction, M:buscaCtaContable");			
		}
		return resp;
	}
}
