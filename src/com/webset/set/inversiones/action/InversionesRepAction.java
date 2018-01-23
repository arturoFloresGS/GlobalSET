package com.webset.set.inversiones.action;

import java.util.ArrayList;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.softwarementors.extjs.djn.config.annotations.DirectMethod;
import com.softwarementors.extjs.djn.servlet.ssm.WebContextManager;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.inversiones.middleware.service.CotizacionesService;
import com.webset.set.inversiones.middleware.service.InversionesComunService;
import com.webset.set.inversiones.middleware.service.InversionesService;
import com.webset.set.inversiones.middleware.service.RepInversionesService;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.Contexto;
import com.webset.set.utilerias.Funciones;
import com.webset.utils.tools.Utilerias;

/**
 * Action para reportes de Inversiones
 * @autor COINSIDE
 */
public class InversionesRepAction {
	
	Bitacora bitacora = new Bitacora();
	Contexto contexto=new  Contexto();
	Funciones funciones = new Funciones();

		
	public List<Map<String, Object>> obtenerReporteCotizacion(String fecha, int noEmpresa, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteCotizacion" + " contexto nulo");
		try{
			CotizacionesService cotizacionesService = (CotizacionesService) contexto.obtenerBean("cotizacionesBusinessImpl", context);
			lista = cotizacionesService.obtenerRepCotizaciones(fecha, noEmpresa);
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerInstitucion");			
		}
		return lista;
	}
	
	/**
	 * OBTIENE LA INFORMACION PARA REPORTE ANALITICO DE INVERSIONES
	 * @param noEmpresa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param anio
	 * @param noInstitucion
	 * @param idDivisa
	 * @param context
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteInversiones(int noEmpresa, String fechaInicial, String fechaFinal, String anio,
			int noInstitucion, String idDivisa, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteInversiones" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			lista = repInversionesService.obtenerReporteInversiones(noEmpresa, fechaInicial, fechaFinal, anio, noInstitucion, idDivisa);
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteInversiones");			
		}
		return lista;
	}
	
	public List<Map<String, Object>> obtenerReporteInvEstablecidas (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteInvEstablecidas" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerReporteInvEstablecidas(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteInvEstablecidas");			
		}
		return lista;
	}
	
	public HSSFWorkbook obtenerExcelInvEstablecidas (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		HSSFWorkbook hb = null;
		List<Map<String, String>>lista = null;

		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteInvEstablecidas" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerExcelInvEstablecidas(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
			hb = Utilerias.generarExcel("INVERSIONES ESTABLECIDAS DEL " + fechaInicial + " AL " + fechaFinal + " EN " + idDivisa,
					"INVERSIONES ESTABLECIDAS",
					new String[]{
					"Día Inversión",
					"Día Vencimiento",					
					"Empresa",
					"Institución",
					"Orden",
					"Papel",
					"Tipo Valor",
					"Plazo",
					"Tasa",
					"Forma de Pago",
					"Capital",
					"Interés",
					"ISR",
					"Capital Vencido"
			}, lista, new String[] {"fec_alta", 
				"fec_venc", 
				"nom_empresa",
				"razon_social",
				"no_orden",
				"id_papel",
				"id_tipo_valor",
				"plazo",
				"tasa",
				"forma_pago",
				"importe",
				"interes",
				"isr",
				"total"}, 
				new String[]{
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00"
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteInvEstablecidas");			
		}
		return hb;
	}
	
	public List<Map<String, Object>> obtenerReporteVencimientos (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteVencimientos" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerReporteVencimientos(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteVencimientos");			
		}
		return lista;
	}
	
	public HSSFWorkbook obtenerExcelVencimientos (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		HSSFWorkbook hb = null;
		List<Map<String, String>>lista = null;

		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteInvEstablecidas" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerExcelVencimientos(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
			hb = Utilerias.generarExcel("VENCIMIENTO DE INVERSIONES DEL " + fechaInicial + " AL " + fechaFinal + " EN " + idDivisa,
					"VENCIMIENTO DE INVERSIONES",
					new String[]{
					"Día Inversión",
					"Día Vencimiento",					
					"Empresa",
					"Institución",
					"Orden",
					"Papel",
					"Tipo Valor",
					"Plazo",
					"Tasa",
					"Estatus",
					"Chequera",
					"Banco Regreso",
					"Chequera Regreso",
					"Capital",
					"Interés",
					"ISR",
					"Capital Vencido"
			}, lista, new String[] {"fec_alta", 
				"fec_venc", 
				"nom_empresa",
				"razon_social",
				"no_orden",
				"id_papel",
				"id_tipo_valor",
				"plazo",
				"tasa",
				"id_estatus_ord",
				"id_chequera",
				"id_banco_reg",
				"id_chequera_reg",
				"importe",
				"interes",
				"isr",
				"total"}, 
				new String[]{
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00"
			});
			
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteInvEstablecidas");			
		}
		return hb;
	}
	
	public HSSFWorkbook obtenerExcelPosicionInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		HSSFWorkbook hb = null;
		List<Map<String, String>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReportePosicionInv" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerExcelPosicionInv(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
			hb = Utilerias.generarExcel("POSICION DE INVERSIONES ESTABLECIDAS DEL " + fechaInicial.replaceAll("/", "_") + " AL " + fechaFinal.replaceAll("/", "_") + " EN " + idDivisa,
					"POSICION DE INVERSIONES",
					new String[]{
					"Día Inversión",
					"Día Vencimiento",					
					"Institución",
					"Orden",
					"Papel",
					"Tipo Valor",
					"Plazo",
					"Tasa",
					"Tasa Eq. 28 días",
					"Forma de Pago",
					"Capital",
					"Interés",
					"Interés Devengado",
					"Interés por Devengar",
					"ISR",
					"Capital Vencido"
			}, lista, new String[] {"fec_alta", 
				"fec_venc", 
				"razon_social",
				"no_orden",
				"id_papel",
				"id_tipo_valor",
				"plazo",
				"tasa",
				"tasa_eq",
				"importe",
				"interes",
				"intereses",
				"int_por_dev",
				"isr",
				"total"}, 
				new String[]{
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					null,
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00"
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReportePosicionInv");			
		}
		return hb;
	}
	
	public List<Map<String, Object>> obtenerReportePosicionInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReportePosicionInv" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerReportePosicionInv(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReportePosicionInv");			
		}
		return lista;
	}
	
	public List<Map<String, Object>> obtenerReporteSaldosInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		List<Map<String, Object>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteVencimientos" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerReporteSaldosInv(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteVencimientos");			
		}
		return lista;
	}
	
	public HSSFWorkbook obtenerExcelSaldosInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal,
			int idUsuario, ServletContext context)
	{
		HSSFWorkbook hb = null;
		List<Map<String, String>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteVencimientos" + " contexto nulo");
		try{
			RepInversionesService repInversionesService = (RepInversionesService) contexto.obtenerBean("repInversionesBusinessImpl", context);
			
			lista = repInversionesService.obtenerExcelSaldosInv(noEmpresa, idDivisa, fechaInicial, fechaFinal, idUsuario);
			
			hb = Utilerias.generarExcel("SALDOS DE INVERSION AL " + fechaInicial.replaceAll("/", "_") + " EN " + idDivisa,
					"SALDOS DE INVERSION",
					new String[]{
					"No. Empresa",
					"Empresa",					
					"Capital",
					"Interés",
					"ISR",
					"Capital Vencido"
			}, lista, new String[] {"no_empresa", 
				"nom_empresa",
				"importe",
				"interes",
				"isr",
				"total"}, 
				new String[]{
					null,
					null,
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00",
					"$#,##0.00"
			});
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteVencimientos");			
		}
		return hb;
	}
	
	public List<Map<String, Object>> obtenerReporteLiquidacion(int noEmpresa, String tipoInversion, ServletContext context)
	{
		
		List<Map<String, Object>>lista = null;
		
		if (contexto == null)
			bitacora.insertarRegistro("P:Inversiones, C:InversionesRepAction, M:obtenerReporteInversiones" + " contexto nulo");
		try{
			InversionesService inversionesService = (InversionesService) contexto.obtenerBean("inversionesBusinessImpl", context);
			lista = inversionesService.obtenerRepLiquidacion(noEmpresa, tipoInversion);
		}catch(Exception e){
			e.printStackTrace();
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:CotizacionesAction, M:obtenerReporteInversiones");			
		}
		
		for (Map<String, Object> map : lista) {
			System.out.println(map);
		}
		System.out.println(lista);
		return lista;
	}
	
	
	@DirectMethod
	public List<LlenaComboValoresDto> obtenerDivisa()
	{
		List<LlenaComboValoresDto>lista=new ArrayList<LlenaComboValoresDto>();
		try{
			if(Utilerias.haveSession(WebContextManager.get()) && Utilerias.tienePermiso(WebContextManager.get(),50)){
				InversionesComunService inversionesComunService = (InversionesComunService) contexto.obtenerBean("inversionesComunBusinessImpl");
				lista = inversionesComunService.consultarDivisas();
			}
		}catch(Exception e){
			bitacora.insertarRegistro(new java.util.Date().toString() + " " + Bitacora.getStackTrace(e)
			+"P:Inversiones, C:InversionesRepAction, M:obtenerDivisa");			
		}
		return lista;
	}
	
	

}
