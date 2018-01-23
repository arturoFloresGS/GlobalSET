package com.webset.set.inversiones.middleware.service;

import java.util.List;
import java.util.Map;

/**
 * Interface de servicios para reportes del modulo de inversiones
 * @author COINSIDE
 *
 */
public interface RepInversionesService 
{
	/**
	 * Obtiene la informacion para el reporte analitico de inversiones
	 * @param noEmpresa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param anio
	 * @param noInstitucion
	 * @param idDivisa
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteInversiones(int noEmpresa, String fechaInicial, String fechaFinal,
			String anio, int noInstitucion, String idDivisa);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Inversiones Establecidas
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteInvEstablecidas (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Vencimiento de Inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteVencimientos (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Posicion de Inversiones Establecidas
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReportePosicionInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Saldos de Inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteSaldosInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Inversiones Establecidas
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, String>> obtenerExcelInvEstablecidas (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Vencimiento de Inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, String>> obtenerExcelVencimientos (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Posicion de Inversiones Establecidas
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, String>> obtenerExcelPosicionInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene lista de mapas con la informacion para el reporte de Saldos de Inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, String>> obtenerExcelSaldosInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
}
