package com.webset.set.monitor.business;

import java.util.List;
import java.util.Map;

public interface MonitorService {
	
	List<String> obtenerArbolPosicionCaja(int idUsuario, String sTicker);
	List<String> obtenerArbolPosicionInversion(int idusuario, String sTicker);
	List<String> obtenerArbolPosicionDeuda(int idUsuario, String sTicker);
	List<String> obtenerArbolConciliaciones(int idUsuario, String sTicker);
	List<Map<String, Object>> graficasMonitor(int idUsuario, String sTicker, int iOpcion);
	String datosTicker();

}
