package com.webset.set.inversiones.dao;

import java.util.List;
import java.util.Map;


/**
 * Interfase DAO para pantalla de Control de Inversiones
 * @author COINSIDE
 *
 */
public interface RepInversionesDao {
	/**
	 * Obtiene los valores de detalle para el reporte de inversiones
	 * @return valores generales para llenado de combo
	 */
	public List<Map<String, Object>> consultarReporteInversiones(int noEmpresa, String fechaInicial, String fechaFinal,
			String anio, int noInstitucion, String idDivisa);
	
	/**
	 * Consulta la informacion para el reporte de inversiones establecidas
	 * @param noEmpresa - Empresa (0 -> Todas)
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @return
	 */
	public List<Map<String, Object>> consultarReporteInvEstablecidas (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Consulta la informacion para el reporte de Vencimiento de inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> consultarReporteVencimiento (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Elimina registros de Tmp_Posicion para el usuario dado
	 * @param idUsuario
	 */
	public void eliminarTmpPosicion (int idUsuario);
	
	/**
	 * Inserta los registros de posicion en TMP_POSICION
	 * @param idUsuario
	 * @param fechaIncial
	 * @param fechaFinal
	 * @param fechaHoy
	 * @param noEmpresa
	 * @param idDivisa
	 */
	public void insertarTmpPosicion (int idUsuario, String fechaIncial, String fechaFinal, int noEmpresa, String idDivisa);
	
	/**
	 * Obtiene la informacion para el reporte de Psicion de Inversiones Establecidas
	 * @param noEmpresa
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> consultarReportePosicionInv (int noEmpresa, int idUsuario);
	
	/**
	 * Obtiene la informacion para el reporte de Saldos de Inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> consultarReporteSaldosInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/*--- EXCEL ---*/
	
	/**
	 * Consulta la informacion para el reporte de inversiones establecidas
	 * @param noEmpresa - Empresa (0 -> Todas)
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @return
	 */
	public List<Map<String, String>> consultarExcelInvEstablecidas (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Consulta la informacion para el reporte de Vencimiento de inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, String>> consultarExcelVencimiento (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	

	/**
	 * Obtiene la informacion para el reporte de Psicion de Inversiones Establecidas
	 * @param noEmpresa
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, String>> consultarExcelPosicionInv (int noEmpresa, int idUsuario);
	
	/**
	 * Obtiene la informacion para el reporte de Saldos de Inversiones
	 * @param noEmpresa
	 * @param idDivisa
	 * @param fechaInicial
	 * @param fechaFinal
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, String>> consultarExcelSaldosInv (int noEmpresa, String idDivisa, String fechaInicial, String fechaFinal, int idUsuario);
	
	/**
	 * Obtiene la información para el reporte de liquidación de inversión (en la pantalla de liquidación)
	 * @param noEmpresa
	 * @param tipoInversion
	 * @return
	 */
	public List<Map<String, Object>> consultarReporteLiqInv (int noEmpresa, String tipoInversion);
	
}
