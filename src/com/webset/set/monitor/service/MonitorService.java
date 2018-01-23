package com.webset.set.monitor.service;

import java.util.List;
import java.util.Map;

public interface MonitorService {

	public String obtenerArbol (String divisa, String opt);
	public List<Map<String, String>> consultaGridDiversos(String divisa);
	public String  consultaIndicadoresBancarios ();
	public List<Map<String, String>> datosGrafica (String tipoGrafica, String divisa);
	
}
