package com.webset.set.financiamiento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.financiamiento.dto.GastoComisionCreditoDto;
import com.webset.set.financiamiento.service.GastosFinanciamientoCService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class GastosFinanciamientoCAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	GastosFinanciamientoCService gastosFinanciamientoCService;
	private GlobalSingleton globalSingleton;

	@DirectMethod
	public List<LlenaComboGralDto> obtenerContratos(String psTipoMenu, int iBanco, int noEmpresa) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				gastosFinanciamientoCService = (GastosFinanciamientoCService) contexto
						.obtenerBean("gastosFinanciamientoCBusinessImpl");
				list = gastosFinanciamientoCService.obtenerContratos(psTipoMenu,iBanco, noEmpresa);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:GastosFinanciamientoCAction, M:obtenerContratos");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerGastos() {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				gastosFinanciamientoCService = (GastosFinanciamientoCService) contexto
						.obtenerBean("gastosFinanciamientoCBusinessImpl");
				list = gastosFinanciamientoCService.obtenerGastos();
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:GastosFinanciamientoCAction, M:obtenerGastos");
		}
		return list;
	}
	@DirectMethod
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				gastosFinanciamientoCService = (GastosFinanciamientoCService) contexto
						.obtenerBean("gastosFinanciamientoCBusinessImpl");
				list = gastosFinanciamientoCService.obtenerDisposiciones(linea,estatus);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:GastosFinanciamientoCAction, M:obtenerDisposiciones");
		}
		return list;
	}
	@DirectMethod
	public List<GastoComisionCreditoDto> selectGastos(String linea, int disposicion) {
		List<GastoComisionCreditoDto> list = new ArrayList<GastoComisionCreditoDto>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				gastosFinanciamientoCService = (GastosFinanciamientoCService) contexto
						.obtenerBean("gastosFinanciamientoCBusinessImpl");
				list = gastosFinanciamientoCService.selectGastos(linea,disposicion);
		//	}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:GastosFinanciamientoCAction, M:obtenerDisposiciones");
		}
		return list;
	}
	@DirectMethod
	public Map<String, Object> altaAmortizacion(String gastos) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		int ultimoIdAmort;
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				gastosFinanciamientoCService = (GastosFinanciamientoCService) contexto
						.obtenerBean("gastosFinanciamientoCBusinessImpl");
						mapResult = gastosFinanciamientoCService.insertAmort(gastos);
		//			}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCAction, M:altaAmortizacion");
		}
		return mapResult;
	}
	@DirectMethod
	public Map<String, Object> eliminarGastos(String gastos) {
		Map<String, Object> mapResult = new HashMap<String, Object>();
		try {
		//	if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
				gastosFinanciamientoCService = (GastosFinanciamientoCService) contexto
						.obtenerBean("gastosFinanciamientoCBusinessImpl");
						mapResult = gastosFinanciamientoCService.eliminarGastos(gastos);
			//		}
		} catch (Exception e) {
			e.printStackTrace();
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:GastosFinanciamientoCAction, M:eliminarGastos");
		}
		return mapResult;
	}
	public List<Map<String, Object>> obtenerReporteGastos(String idLinea,int idDisposicion, ServletContext context) {
		List<Map<String, Object>> lista = null;
		if (contexto == null) {
			bitacora.insertarRegistro(
					"P:Financiamiento, C:GastosFinanciamientoCAction, M:obtenerReporteContratos" + " contexto nulo");
			return null;
		}
		try {
			GastosFinanciamientoCService gastosFinanciamientoCService = (GastosFinanciamientoCService) contexto
					.obtenerBean("gastosFinanciamientoCBusinessImpl", context);
			lista = gastosFinanciamientoCService.obtenerReporteGastos(idLinea,idDisposicion);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
					+ "P:Financiamiento, C:GastosFinanciamientoCAction, M:obtenerReporteContratos");
		}
		return lista;
	}
}