package com.webset.set.barridosfondeos.service;

import java.util.List;
import java.util.Map;

import com.webset.set.barridosfondeos.dto.FondeoAutomaticoDto;


public interface BarridosFondeosRepService {
	/**
	 * Obtiene la lista de mapas con la información del reporte de Arboles
	 * @param noEmpresaRaiz
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteArbol(int noEmpresaRaiz);
	
	/**
	 * Obtiene la lista de mapas con la información del reporte de Fondeo Automatico
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteFondeo(int idUsuario, String tipoOperacion);
	
	/**
	 * Obtiene el reporte integrado de barridos y fondeos que se van a realizar - Este reporte no existe en la version VB
	 * @param barridos - barridos y fondeos que se desea ejecutar
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteFondeoBarridoAut(List<FondeoAutomaticoDto> barridos);
	
	/**
	 * Obtiene la lista de mapas con la información del reporte de Arboles
	 * @param noEmpresaRaiz
	 * @param idUsuario
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteArbolEstruc(int noEmpresaRaiz, int idUsuario);
	
	/**
	 * Obtiene una lista de mapas con la informacion del reporte de empresas filiales
	 * @param noEmpresa
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteFiliales (int noEmpresa);
	
	/**
	 * Obtiene una lista de mapas con la información para reporte de barridos y fondeos
	 * @param noEmpresa
	 * @param idUsuario
	 * @param fecha
	 * @return
	 */
	public List<Map<String, Object>> obtenerReporteBarridosFondeos(int noEmpresa, int idUsuario, String fecha);
	
	/**
	 * Obtiene una lista de mapas de string con la información para reporte de barridos y fondeos
	 * @param noEmpresa
	 * @param idUsuario
	 * @param fecha
	 * @return
	 */
	public List<Map<String, String>> obtenerReporteBarridosFondeosStr(int noEmpresa, int idUsuario, String fecha);

	/**
	 * Obtiene una lista de mapas de string con la información para reporte de cuadre de fondeo
	 * @param noEmpresa
	 * @param idUsuario
	 * @param fecha
	 * @return
	 */
	public List<Map<String, String>> obtenerReporteCuadreFondeo(int noEmpresa, int idUsuario, String fecha);

}
