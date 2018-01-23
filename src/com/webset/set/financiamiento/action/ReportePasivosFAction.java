package com.webset.set.financiamiento.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.webset.set.financiamiento.dto.ReportePasivosFDto;
import com.webset.set.financiamiento.service.ReportePasivosFService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.set.utilerias.GlobalSingleton;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public class ReportePasivosFAction {

	Bitacora bitacora = new Bitacora();
	Contexto contexto = new Contexto();
	Funciones funciones = new Funciones();
	ReportePasivosFService reportePasivosFService;
	private GlobalSingleton globalSingleton;

	@DirectMethod
	public List<LlenaComboGralDto> obtenerEmpresas(int plUsuario,String psMenu) {
		List<LlenaComboGralDto> list = new ArrayList<LlenaComboGralDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reportePasivosFService = (ReportePasivosFService) contexto
					.obtenerBean("reportePasivosFBusinessImpl");
			list = reportePasivosFService.obtenerEmpresas(plUsuario,psMenu);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ReportePasivosFAction, M:obtenerEmpresas");
		}
		return list;
	}
	@DirectMethod
	public List<ReportePasivosFDto> obtenerDivisaCreditos(int plUsuario,String psMenu) {
		List<ReportePasivosFDto> list = new ArrayList<ReportePasivosFDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reportePasivosFService = (ReportePasivosFService) contexto
					.obtenerBean("reportePasivosFBusinessImpl");
			list = reportePasivosFService.obtenerDivisaCreditos(plUsuario,psMenu);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, CR:ReportePasivosFAction, M:obtenerEmpresas");
		}
		return list;
	}
	@DirectMethod
	public List<ReportePasivosFDto> obtenerPasivosFinancieros(int noEmpresa, String json) {
		List<ReportePasivosFDto> list = new ArrayList<ReportePasivosFDto>();
		try {
			//if (Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(), 50)) {
			reportePasivosFService = (ReportePasivosFService) contexto
					.obtenerBean("reportePasivosFBusinessImpl");
			list = reportePasivosFService.obtenerPasivosFinancieros(noEmpresa,json);
			//}
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ReportePasivosFAction, M:obtenerPasivosFinancieros");
		}
		return list;
	}
	
	@DirectMethod
	public HSSFWorkbook reportePasivosFinancieros(String pasivos,
			ServletContext context) {
		HSSFWorkbook wb = null;
		try {
			reportePasivosFService = (ReportePasivosFService) contexto
					.obtenerBean("reportePasivosFBusinessImpl", context);
			wb = reportePasivosFService.reportePasivosFinancieros(pasivos);
		} catch (Exception e) {
			bitacora.insertarRegistro(new Date().toString() + " " + Bitacora.getStackTrace(e)
			+ "P:Financiamiento, C:ReportePasivosFAction, M:reportePasivosFinancieros");
		}
		return wb;
	}
}