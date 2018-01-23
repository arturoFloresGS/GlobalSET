package com.webset.set.financiamiento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.service.AvalesGarantiasFCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class AvalesGarantiasFCAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	AvalesGarantiasFCService avalesGarantiasFCService;
	private GlobalSingleton globalSingleton;

	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario,boolean pbMismaEmpresa, int plEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			list = avalesGarantiasFCService.llenarCmbEmpresa(piNoUsuario,pbMismaEmpresa,plEmpresa);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:llenarCmbEmpresa");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbEmpresaAvalista(int piNoUsuario,boolean pbMismaEmpresa, int plEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			list = avalesGarantiasFCService.llenarCmbEmpresaAvalista(piNoUsuario,pbMismaEmpresa,plEmpresa);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:llenarCmbEmpresaAvalista");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> llenarCmbTipoGtia() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			list = avalesGarantiasFCService.llenarCmbTipoGtia();
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:llenarCmbEmpresa");
		}
		return list;
	}
	@DirectMethod
	public List<AvalGarantiaDto> buscarAvalGtia(String psTipo,String piEmpresa) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			list = avalesGarantiasFCService.buscarAvalGtia(psTipo,piEmpresa);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:llenarCmbEmpresa");
		}
		return list;
	}
	@DirectMethod
	public  Map<String, Object> updateAvalGarantia(String empresa, int lsTipo,String clave,String descripcion, 
			double valor, String fecIni,String fecFin, double pje, String vsEspecial) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			mapResult = avalesGarantiasFCService.updateAvalGarantia( empresa,  lsTipo, clave, descripcion, 
					valor,  fecIni, fecFin,  pje,  vsEspecial);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:updateAvalGarantia");
		}
		return mapResult;
	}
	@DirectMethod
	public  Map<String, Object> insertaAvalGtia(String empresa, int lsTipo,String clave,String descripcion,
			double valor, String idDivisa,String fecIni,String fecFin, double pje, String vsEspecial) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			mapResult = avalesGarantiasFCService.insertaAvalGtia( empresa,  lsTipo, clave, descripcion, 
					valor, idDivisa, fecIni, fecFin,  pje,  vsEspecial);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:insertaAvalGtia");
		}
		return mapResult;
	}
	@DirectMethod
	public List<AvalGarantiaDto> selectAvaladas(String empresa,String clave) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			list = avalesGarantiasFCService.selectAvaladas(empresa,clave);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:selectAvaladas");
		}
		return list;
	}
	@DirectMethod
	public  Map<String, Object> insertaAsignacionEmp(String empresa,String clave,int empresaA,
			double montoAvalado) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			mapResult = avalesGarantiasFCService.insertaAsignacionEmp(empresa,clave,empresaA, 
					montoAvalado);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:insertaAvalGtia");
		}
		return mapResult;
	}
	@DirectMethod
	public  Map<String, Object> existeAvalGtiaLinea(String empresa,String clave,int empresaA) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			mapResult = avalesGarantiasFCService.existeAvalGtiaLinea(empresa,clave,empresaA);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:existeAvalGtiaLinea");
		}
		return mapResult;
	}
	@DirectMethod
	public  Map<String, Object> deleteAvalada(String empresa,String clave,int empresaA) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
			// if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			mapResult = avalesGarantiasFCService.deleteAvalada(empresa,clave,empresaA);
			//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:existeAvalGtiaLinea");
		}
		return mapResult;
	}
	@DirectMethod
	public List<AvalGarantiaDto> reporteAvalesGtiasAvaladas(int tipo) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			list = avalesGarantiasFCService.reporteAvalesGtiasAvaladas(tipo);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:selectAvaladas");
		}
		return list;
	}

	@DirectMethod
	public HSSFWorkbook excelAvaladas(String avaladas,ServletContext context) {
		HSSFWorkbook wb = null;
		try {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl", context);
			wb = avalesGarantiasFCService.excelAvaladas(avaladas);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:excelAvaladas");
		}
		return wb;
	}
	@DirectMethod
	public List<AvalGarantiaDto> reporteAvalesGtiasAvalistas(int tipo) {
		List<AvalGarantiaDto> list = new ArrayList<AvalGarantiaDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl");
			list = avalesGarantiasFCService.reporteAvalesGtiasAvalistas(tipo);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:selectAvaladas");
		}
		return list;
	}
	@DirectMethod
	public HSSFWorkbook excelAvalistas(String avaladas,ServletContext context) {
		HSSFWorkbook wb = null;
		try {
			avalesGarantiasFCService = (AvalesGarantiasFCService) contexto
					.obtenerBean("avalesGarantiasFCBusinessImpl", context);
			wb = avalesGarantiasFCService.excelAvalistas(avaladas);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:AvalesGarantiasFCAction, M:excelAvalistas");
		}
		return wb;
	}
	public List<Map<String, Object>> reporteAvalistas(String empresas, String noEmpresa) {
		List<Map<String, Object>> lista =  new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		List<Map<String, String>> params= gson.fromJson(empresas,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		try {
			for (int i = 0; i < params.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("empresa",noEmpresa);
				if(params.get(i).get("nomEmpresa")==null)
					map.put("nomEmpresa","");
				else
					map.put("nomEmpresa",params.get(i).get("nomEmpresa"));
				if(params.get(i).get("descripcion")==null)
					map.put("descripcion","");
				else
					map.put("descripcion",params.get(i).get("descripcion"));
				if(params.get(i).get("montoAvaladoS")==null)
					map.put("montoAvaladoS","");
				else
					map.put("montoAvaladoS",params.get(i).get("montoAvaladoS"));
				if(params.get(i).get("dispuesto")==null)
					map.put("dispuesto","");
				else
					map.put("dispuesto",params.get(i).get("dispuesto"));
				if(params.get(i).get("dispuestoReal")==null)
					map.put("dispuestoReal","");
				else
					map.put("dispuestoReal",params.get(i).get("dispuestoReal"));
				if(params.get(i).get("montoDisponible")==null)
					map.put("montoDisponible","");
				else
					map.put("montoDisponible",params.get(i).get("montoDisponible"));
				if(params.get(i).get("disponibleReal")==null)
					map.put("disponibleReal","");
				else
					map.put("disponibleReal",params.get(i).get("disponibleReal"));
				lista.add(map);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:reporteAvalistas");
		}
		return lista;

	}
	
	public List<Map<String, Object>> reporteAvaladas(String empresas, String noEmpresa) {
		List<Map<String, Object>> lista =  new ArrayList<Map<String, Object>>();
		Gson gson = new Gson();
		List<Map<String, String>> params= gson.fromJson(empresas,
				new TypeToken<ArrayList<Map<String, String>>>() {
		}.getType());
		try {
			for (int i = 0; i < params.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("empresa",noEmpresa);
				if(params.get(i).get("nomEmpresa")==null)
					map.put("nomEmpresa","");
				else
					map.put("nomEmpresa",params.get(i).get("nomEmpresa"));
				if(params.get(i).get("descripcion")==null)
					map.put("descripcion","");
				else
					map.put("descripcion",params.get(i).get("descripcion"));
				if(params.get(i).get("garantiaAsignada")==null)
					map.put("garantiaAsignada","");
				else
					map.put("garantiaAsignada",params.get(i).get("garantiaAsignada"));
				if(params.get(i).get("montoAsignado")==null)
					map.put("montoAsignado","");
				else
					map.put("montoAsignado",params.get(i).get("montoAsignado"));
				if(params.get(i).get("descBanco")==null)
					map.put("descBanco","");
				else
					map.put("descBanco",params.get(i).get("descBanco"));
				if(params.get(i).get("idFinanciamiento")==null)
					map.put("idFinanciamiento","");
				else
					map.put("idFinanciamiento",params.get(i).get("idFinanciamiento"));
				if(params.get(i).get("credito")==null)
					map.put("credito","");
				else
					map.put("credito",params.get(i).get("credito"));
				if(params.get(i).get("montoAvaladoS")==null)
					map.put("montoAvaladoS","");
				else
					map.put("montoAvaladoS",params.get(i).get("montoAvaladoS"));
				if(params.get(i).get("dispuesto")==null)
					map.put("dispuesto","");
				else
					map.put("dispuesto",params.get(i).get("dispuesto"));
				if(params.get(i).get("montoDisponible")==null)
					map.put("montoDisponible","");
				else
					map.put("montoDisponible",params.get(i).get("montoDisponible"));
				lista.add(map);
			}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:VencimientoFinanciamientoCAction, M:reporteAvalistas");
		}
		return lista;

	}


}