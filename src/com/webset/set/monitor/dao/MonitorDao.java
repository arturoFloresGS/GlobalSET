package com.webset.set.monitor.dao;

import java.util.List;
import java.util.Map;

public interface MonitorDao {
	
	public List<Map<String,Object>> obtenerArbolCaja(String divisa);
	public List<Map<String,Object>> obtenerArbolInversion (String divisa);
	public List<Map<String,Object>> obtenerArbolDeudas (String divisa);
	public List<Map<String,Object>> obtenerArbolConciliaciones (String divisa);
	public List<Map<String,Object>>  obtenerArbolComparativo (String divisa);
	public String  calcularGlobal (String divisa);
	
	public List<Map<String,Object>>  consultaGridDiversos (String divisa);
	public List<Map<String,Object>>  consultaIndicadoresBancarios ();

	public List<Map<String, Object>> graficaPoscionCaja(String divisa);
	public List<Map<String, Object>> graficaPoscionConciliacion(String divisa);
	public List<Map<String, Object>> graficarPoscionInversion(String divisa);
	public List<Map<String, Object>> graficarPoscionDeuda(String divisa);
	
}
