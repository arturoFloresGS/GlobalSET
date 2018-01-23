package com.webset.set.inversiones.middleware.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import com.webset.set.inversiones.dto.CanastaInversionesDto;

public interface BarridoInversionService {
	public List<CanastaInversionesDto> empresasConcentradoras();
	public List<CanastaInversionesDto> todosLosBancos(String idDivisa);
	public List<CanastaInversionesDto> obtenerRegistros(String idDivisa, String noBanco);
	public String insertarBarridos(String params);
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++ PROCESO DE EJECUCION DE BARRIDO INVERSION +++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public List<CanastaInversionesDto> obtenerSolicitudesBarrido(String idDivisa, String noBanco);
	public String confirmaBarrido(String noSolicitudes, String opcion, String clave);
	public String regresarBarrido(String noSolicitudes);
	public String ejecutarBarridos(String noSolicitudes);
		
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++        MONITOR DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public List<CanastaInversionesDto> obtenerCanastasInv(String noEmpresa, String idDivisa);
	public String crearNodosArbol(String noEmpresa, String idDivisa);
	public String vencimientoCanastas(String param, int noEmpresa, String tasaReal);
	public String clasificacionCanastas(int canasta, int noEmpresa, String codigo);
	public List<CanastaInversionesDto> consultarDetalleCanasta(int canasta);
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++       REPORTES DE INVERSION        ++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	@SuppressWarnings("unchecked")
	public JRDataSource reportesCanasta(Map datos);
	
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//++++++++++++++++++++++++++       TASAS Y COMISION DE INVERSION        ++++++++++++++++++++++++++
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	
	public List<CanastaInversionesDto> obtenerTasaComision();
	public String insertarTasaComision(String param, String comision);
	public List<CanastaInversionesDto> obtenerCanastasVencidasHoy(
			String noEmpresa, String idDivisa);
}
