package com.webset.set.interfaz.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.interfaz.service.ExportacionPolizasService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.utils.tools.Utilerias;

public class ExportacionPolizasAction {
	private Bitacora bitacora = new Bitacora();
	private Contexto contexto = new  Contexto();
	Funciones funciones = new Funciones();
	@DirectMethod
	public List<LlenaComboGralDto> llenarComboGrupoEmpresas(int idUsuario){
		GrupoEmpresasDto dtoIn= new GrupoEmpresasDto();
		List<LlenaComboGralDto> listRet= new ArrayList<LlenaComboGralDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),200))
			return listRet;
		try{
			ExportacionPolizasService exportacionPolizasService = (ExportacionPolizasService)contexto.obtenerBean("exportacionPolizasBusiness");
			dtoIn.setIdUsuario("" + idUsuario);
			listRet = exportacionPolizasService.llenarComboGrupoEmpresa(dtoIn); 
		}catch(Exception e){
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+"P:Interfaz, C:ExportacionPolizasAction, M:llenarComboGrupoEmpresas");	
		}
		return listRet;
	}
	
	@DirectMethod
	public List<MovimientoDto> consultaPolizasExportar(String empresa, String origen, String fecInicio, String fecFin){
		System.out.println("Action consulta de pagos");
		List<MovimientoDto> result = new ArrayList<MovimientoDto>();
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),200))
			return result;
		try {
			ExportacionPolizasService exportacionPolizasService = (ExportacionPolizasService)contexto.obtenerBean("exportacionPolizasBusiness");
			result = exportacionPolizasService.consultaPolizasExportar(empresa, origen, fecInicio, fecFin);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasAction, M:consultaPolizasExportar");	
		}
		return result;
	}
	
	@DirectMethod 
	public Map<String, Object> ejecutarExportacionPolizas (String data){
		Map<String, Object> result = new HashMap<String,Object>();
		if(!Utilerias.haveSession(WebContextManager.get()) && !Utilerias.tienePermiso(WebContextManager.get(),200))
			return result;
		result.put("mensaje", "Error desconocido.");
		Gson gson = new Gson();
		List<Map<String, String>> objParams = gson.fromJson(data, new TypeToken<ArrayList<Map<String, String>>>() {}.getType());
		System.out.println(objParams);
		List<MovimientoDto>listGrid= new ArrayList<MovimientoDto>();
		try {
			for (int i = 0; i < objParams.size(); i++) {
				MovimientoDto movimiento = new MovimientoDto();
				movimiento.setIdGrupoCupo(objParams.get(i).get("idGrupoCupo") != null && !objParams.get(i).get("idGrupoCupo").equals("") ? objParams.get(i).get("idGrupoCupo"):"");
				movimiento.setIdRubro(objParams.get(i).get("idRubro") != null && !objParams.get(i).get("idRubro").equals("") ? Double.parseDouble(objParams.get(i).get("idRubro")):0);
				movimiento.setIdBanco(objParams.get(i).get("idBanco") != null && !objParams.get(i).get("idBanco").equals("") ? Integer.parseInt(objParams.get(i).get("idBanco")):0);
				movimiento.setNoEmpresa(objParams.get(i).get("noEmpresa") != null && !objParams.get(i).get("noEmpresa").equals("") ? Integer.parseInt(objParams.get(i).get("noEmpresa")):0);
				movimiento.setImporte(objParams.get(i).get("importe") != null && !objParams.get(i).get("importe").equals("") ? Double.parseDouble(objParams.get(i).get("importe")):0);
				movimiento.setIdFormaPago(objParams.get(i).get("idFormaPago") != null && !objParams.get(i).get("idFormaPago").equals("") ? Integer.parseInt(objParams.get(i).get("idFormaPago")):0);
				movimiento.setNoFolioDet(objParams.get(i).get("noFolioDet") != null && !objParams.get(i).get("noFolioDet").equals("") ? Integer.parseInt(objParams.get(i).get("noFolioDet")):0);
				movimiento.setIdDivisa(objParams.get(i).get("idDivisa") != null && !objParams.get(i).get("idDivisa").equals("") ? objParams.get(i).get("idDivisa"):"");
				movimiento.setReferencia(objParams.get(i).get("referencia") != null && !objParams.get(i).get("referencia").equals("") ? objParams.get(i).get("referencia"):"");
				movimiento.setNoDocto(objParams.get(i).get("noDocto") != null && !objParams.get(i).get("noDocto").equals("") ? objParams.get(i).get("noDocto"):"");
				movimiento.setNoPoliza(objParams.get(i).get("noPoliza") != null && !objParams.get(i).get("noPoliza").equals("") ? Integer.parseInt(objParams.get(i).get("noPoliza")):0);
				movimiento.setFecAlta(objParams.get(i).get("fecAlta") != null && !objParams.get(i).get("fecAlta").equals("") ? funciones.ponerFechaDate(objParams.get(i).get("fecAlta")): null );
				movimiento.setFecValor(objParams.get(i).get("fecValor") != null && !objParams.get(i).get("fecValor").equals("") ? funciones.ponerFechaDate(objParams.get(i).get("fecValor")): null );
				movimiento.setDivision(objParams.get(i).get("division") != null && !objParams.get(i).get("division").equals("") ? objParams.get(i).get("division"):"");
				movimiento.setEquivale(objParams.get(i).get("equivale") != null && !objParams.get(i).get("equivale").equals("") ? objParams.get(i).get("equivale"):"");
				movimiento.setIdDivisaOriginal(objParams.get(i).get("idDivisaOriginal") != null && !objParams.get(i).get("idDivisaOriginal").equals("") ? objParams.get(i).get("idDivisaOriginal"):"");
				movimiento.setNoCliente(objParams.get(i).get("noCliente") != null && !objParams.get(i).get("noCliente").equals("") ? objParams.get(i).get("noCliente"):"");
				movimiento.setCab(objParams.get(i).get("cab") != null && !objParams.get(i).get("cab").equals("") ? objParams.get(i).get("cab"):"");
				movimiento.setConcepto(objParams.get(i).get("concepto") != null && !objParams.get(i).get("concepto").equals("") ? objParams.get(i).get("concepto"):"");
				listGrid.add(movimiento);
			}
			ExportacionPolizasService exportacionPolizasService = (ExportacionPolizasService)contexto.obtenerBean("exportacionPolizasBusiness");
			result = exportacionPolizasService.ejecutarExportacionPolizas(listGrid);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Interfaz, C:ExportacionPolizasAction, M:ejecutarExportacionPolizas");	
		}
		return result;
	}

}
