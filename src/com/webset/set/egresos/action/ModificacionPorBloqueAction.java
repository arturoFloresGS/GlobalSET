/*
@author: Geraldine Xareni González Mora
@since: 01/02/2017
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
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.egresos.dto.SolicitudPagoDto;
import com.webset.set.egresos.service.ModificacionPorBloqueService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;
import com.webset.utils.tools.Utilerias;

public class ModificacionPorBloqueAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	ModificacionPorBloqueService modificacionPorBloqueService;
	private GlobalSingleton globalSingleton;

	@DirectMethod
	public List<LlenaComboDivisaDto> obtenerDivisas() {
		List<LlenaComboDivisaDto> list = new ArrayList<LlenaComboDivisaDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
						.obtenerBean("modificacionPorBloqueBusinessImpl");
				list = modificacionPorBloqueService.obtenerDivisas();
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:obtenerDivisas");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
						.obtenerBean("modificacionPorBloqueBusinessImpl");
				list = modificacionPorBloqueService.obtenerBancos(noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:obtenerBancos");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerDivision(int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
						.obtenerBean("modificacionPorBloqueBusinessImpl");
				list = modificacionPorBloqueService.obtenerDivision(noEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:obtenerDivision");
		}
		return list;
	}

	@DirectMethod
	public List<SolicitudPagoDto> obtenerSolicitudes(int iIdBanco, int iNoEmpresa, String sStatus, String sDivisa,
			String sDivision, int iFormaPago, int cTodas, String sFecFin, String sFecIni, String uMontoFin,
			String uMontoIni, String iNoProveedor) {
		List<SolicitudPagoDto> solicitudes = new ArrayList<SolicitudPagoDto>();
		CriteriosBusquedaDto dto = new CriteriosBusquedaDto();
		//if (!Utilerias.haveSession(WebContextManager.get()))
		//	return null;
		try {
			//if (Utilerias.haveSession(WebContextManager.get())) {
				modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
						.obtenerBean("modificacionPorBloqueBusinessImpl");
				dto.setIdBanco(iIdBanco);
				if (cTodas == 0)
					dto.setNoEmpresa(iNoEmpresa);
				else
					dto.setNoEmpresa(0);
				dto.setEstatus(sStatus);
				dto.setIdDivisa(sDivisa);
				dto.setDivision(sDivision);
				dto.setFormaPago(iFormaPago);
				if (!sFecIni.equals(""))
					dto.setFechaIni(funciones.ponerFechaDate(sFecIni));
				if (!sFecFin.equals(""))
					dto.setFechaFin(funciones.ponerFechaDate(sFecFin));
				dto.setMontoIni(funciones.convertirCadenaDouble(uMontoIni.trim()));
				dto.setMontoFin(funciones.convertirCadenaDouble(uMontoFin.trim()));
				dto.setBeneficiario(iNoProveedor);
				solicitudes = modificacionPorBloqueService.obtenerSolicitudes(dto);
		//	}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:obtenerSolicitudes");
		}
		return solicitudes;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerProveedores(String texto, int iNoEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
						.obtenerBean("modificacionPorBloqueBusinessImpl");
				list = modificacionPorBloqueService.obtenerProveedores(texto, iNoEmpresa);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:llenarComboProveedores");
		}
		return list;
	}

	@DirectMethod
	public List<LlenaComboGralDto> obtenerBeneficiario(int iNoEmpresa, String texto) {

		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
						.obtenerBean("modificacionPorBloqueBusinessImpl");
				list = modificacionPorBloqueService.obtenerBeneficiario(iNoEmpresa, texto);
				if (list.isEmpty())
					list = modificacionPorBloqueService.obtenerBeneficiario2(iNoEmpresa, texto);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:obtenerBeneficiario");
		}
		return list;

	}

	@DirectMethod
	public Map<String, Object> ejecutar(String solicitudes, int concentradora, String fecha, int formaPago,
			String concepto) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		Map<String, Object> mapCuadrante = new HashMap<String, Object>();
		try {
			StoreParamsComunDto cambiaCuadranteDto = new StoreParamsComunDto();
			globalSingleton = GlobalSingleton.getInstancia();
			boolean pb_FPago = false;
			int iBancoBenef;
			String sChequeraBenef = "";
			String sIdDivisaBenef = "";
			String ps_folios = "";
			String ps_folios_rechazados = "";
			String ps_folios_no_cliente = "";
			boolean pb_FormaPagoBenef;
			int lAfectados;
		//	if (!Utilerias.haveSession(WebContextManager.get()))
		//		return null;
			Gson gson = new Gson();
			List<Map<String, String>> param = gson.fromJson(solicitudes,
					new TypeToken<ArrayList<Map<String, String>>>() {
					}.getType());

			List<String> mensajes = new ArrayList<String>();
			//if (Utilerias.haveSession(WebContextManager.get())) {

				if (formaPago > 0) {
					pb_FPago = true;
				}

				for (int i = 0; i < param.size(); i++) {

					if (pb_FPago) {
						if (param.get(i).get("noCliente") != "") {
							pb_FormaPagoBenef = false;
							modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
									.obtenerBean("modificacionPorBloqueBusinessImpl");
							pb_FormaPagoBenef = modificacionPorBloqueService
									.validarFormaPagoBenef(Integer.parseInt(param.get(i).get("noCliente")), formaPago);
							if (pb_FormaPagoBenef) {
								if (formaPago == 3 || formaPago == 7) {
									iBancoBenef = 0;
									sChequeraBenef = "";
									sIdDivisaBenef = "";
									List<SolicitudPagoDto> rstBancoChequera = new ArrayList<SolicitudPagoDto>();
									rstBancoChequera = modificacionPorBloqueService.validarBancoCheqBenef(
											Integer.parseInt(param.get(i).get("noCliente")),
											param.get(i).get("idDivisa"), 0);
									if (!rstBancoChequera.isEmpty()) {
										iBancoBenef = rstBancoChequera.get(0).getIdBanco();
										sChequeraBenef = rstBancoChequera.get(0).getIdChequera();
										lAfectados = 0;
										lAfectados = modificacionPorBloqueService.actualizaBancoCheqBenef(
												Integer.parseInt(param.get(i).get("noFolioDet")), iBancoBenef,
												sChequeraBenef);
										if (lAfectados > 0) {
											ps_folios = ps_folios + Integer.parseInt(param.get(i).get("noFolioDet"))
													+ ",";
										}
									} else {
										ps_folios_rechazados = ps_folios_rechazados
												+ Integer.parseInt(param.get(i).get("noFolioDet")) + ",";
									}
								} else {
									if (param.get(i).get("idLeyenda").equals("1") && formaPago == 1) {
										lAfectados = modificacionPorBloqueService.actualizaLimpiaBancoCheqBenef(
												Integer.parseInt(param.get(i).get("noFolioDet")));
									}
									ps_folios = ps_folios + Integer.parseInt(param.get(i).get("noFolioDet")) + ",";
								}
							} else {
								ps_folios_rechazados = ps_folios_rechazados
										+ Integer.parseInt(param.get(i).get("noFolioDet")) + ",";
							}
						} else {
							ps_folios_no_cliente = ps_folios_no_cliente
									+ Integer.parseInt(param.get(i).get("noFolioDet")) + ",";
						}
					} else {
						ps_folios = ps_folios + Integer.parseInt(param.get(i).get("noFolioDet")) + ",";

					}
				}
				if (ps_folios != "") {
					mensajes.add(ps_folios);
					mapCuadrante = new HashMap<String, Object>();
					cambiaCuadranteDto = new StoreParamsComunDto();
					String fechaM;
					System.out.println("fecha"+fecha);
				if(!fecha.equals("")&&!fecha.equals(null)){
					System.out.println("entrs");
					 fechaM= funciones.cambiarOrdenFecha(fecha);
				}
				else{
				
					fechaM="";
				}
					cambiaCuadranteDto.setParametro(globalSingleton.getUsuarioLoginDto().getIdUsuario() + ","
							+ formaPago + "," + fechaM + "," + concepto + "," + "" + "," + 0 + ","
							+ ps_folios.substring(0, ps_folios.length() - 1));
					modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
							.obtenerBean("modificacionPorBloqueBusinessImpl");
					mapCuadrante = modificacionPorBloqueService.cambiarCuadrantes(cambiaCuadranteDto);

					if (Integer.parseInt(mapCuadrante.get("result").toString()) > 0) {
						mensajes.add("Error al ajustar los cuadrantes " + ps_folios);
						bitacora.insertarRegistro(
								new Date().toString() + "P:Egresos, C:PagoPropuestasBusiness, M:agruparMovimientos "
										+ globalSingleton.getUsuarioLoginDto().getIdUsuario()
										+ "Error al ajustar los cuadrantes(SP_ajustes) " + ps_folios);
					}
					mensajes.add("Datos Registrados.");
				}
				if (ps_folios_rechazados != "") {
					mensajes.add("Folios rechazados : "
							+ ps_folios_rechazados.substring(0, ps_folios_rechazados.length() - 1)
							+ " forma de pago NO AUTORIZADA.");
				}
				if (ps_folios_no_cliente != "") {
					mensajes.add("Folios rechazados : "
							+ ps_folios_no_cliente.substring(0, ps_folios_no_cliente.length() - 1)
							+ " NO SE LES PUEDE CAMBIAR LA FORMA DE PAGO.");
				}
				mapResult.put("msgUsuario", mensajes);
			//}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:ejecutar ");
		}
		return mapResult;
	}

	@DirectMethod
	public Map<String, Object> bloquear(String solicitudes, int concentradora) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		String ps_folios;
		String ps_folios_rechazados;
		String ps_folios_no_cliente;
		String ps_bloqueo;
		int lAfectados;
		try {
			globalSingleton = GlobalSingleton.getInstancia();
			ps_folios = "";
			ps_folios_rechazados = "";
			ps_folios_no_cliente = "";
			ps_bloqueo = "";
		//	if (!Utilerias.haveSession(WebContextManager.get()))
		//		return null;
			Gson gson = new Gson();
			List<Map<String, String>> param = gson.fromJson(solicitudes,
					new TypeToken<ArrayList<Map<String, String>>>() {
					}.getType());
			List<String> mensajes = new ArrayList<String>();
		//	if (Utilerias.haveSession(WebContextManager.get())) {
				for (int i = 0; i < param.size(); i++) {

					if (param.get(i).get("codBloqueo").equals("") || param.get(i).get("codBloqueo").equals("N")) {
						ps_bloqueo = "S";
					}
					if (param.get(i).get("codBloqueo").equals("S")) {
						ps_bloqueo = "N";

					}
					lAfectados = 0;
					modificacionPorBloqueService = (ModificacionPorBloqueService) contexto
							.obtenerBean("modificacionPorBloqueBusinessImpl");
					lAfectados = modificacionPorBloqueService
							.modificarBloqueo(Integer.parseInt(param.get(i).get("noFolioDet")), ps_bloqueo);
					String mje;
					if (ps_bloqueo == "N")
						mje = "DESBLOQUEO MANUAL";
					else
						mje = "BLOQUEO MANUAL";
					bitacora.insertarRegistro(
							new Date().toString() + "P:Egresos, C:ModificacionPorBloqueAction, M:bloquear "
									+ globalSingleton.getUsuarioLoginDto().getIdUsuario() + "Folio "
									+ Integer.parseInt(param.get(i).get("noFolioDet")) + " " + mje);
					if (lAfectados > 0)
						ps_folios = ps_folios + Integer.parseInt(param.get(i).get("noFolioDet")) + ",";
				}
				bitacora.insertarRegistro(
						new Date().toString() + "P:Egresos, C:ModificacionPorBloqueAction, M:bloquear "
								+ globalSingleton.getUsuarioLoginDto().getIdUsuario()
								+ "Error al modificar el codBloqueo " + ps_folios);
		//	}
			mapResult.put("msgUsuario", mensajes);
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Egresos, C:ModificacionPorBloqueAction, M:bloquear ");
		}
		return mapResult;
	}

}
