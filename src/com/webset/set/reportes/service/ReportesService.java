package com.webset.set.reportes.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;


public interface ReportesService {
	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario);
	public List<LlenaComboGralDto> consultarCajas(int usuario);
	public List<LlenaComboChequeraDto> consultarOrigen(String tipoMovto);
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, String sDivisa);
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa);
	public JRDataSource obtenerDatosReporteCheques(String nomReporte, Map parameters);
	public int buscarDatosReportes(List<Map<String, String>> objParams);
	public JRDataSource reporteTransConfirmadas(Map parameters);
	public String exportaExcel(String datos);
	public String exportaExcelTransfer(String datos);
}
