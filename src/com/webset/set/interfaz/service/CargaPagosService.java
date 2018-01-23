package com.webset.set.interfaz.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.interfaz.dto.CargaPagosDto;

public interface CargaPagosService 
{
	
	public List<CargaPagosDto> llenaComboEmpresas(int noUsuario);
	public List<CargaPagosDto> llenaGrid(int iTipoPago, String fecHoy, int usuarioALta, String noEmpresa, String estatus, String fecini, String fecFin);
	public String insertaRegistros(List<Map<String, String>> lista);	
	public String insertaRegistrosCXP(String noEmpresa);
	public String armaPropuesta(List<Map<String, String>> gridDatos);
	public int validaFacultad(int facultad);
	public List<CargaPagosDto> llenaGridCXC(int noEmpresa, String estatus);
	public String insertaRegistrosCXC(List<Map<String, String>> objCamposGrid);
	public HSSFWorkbook reporteInterfaces(String tipoValor, String fecHoy, String usuarioAlta, String idEmpresa,
			String estatus, String fecIni, String fecFin);
}
