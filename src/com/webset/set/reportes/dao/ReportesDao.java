package com.webset.set.reportes.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

@SuppressWarnings("unchecked")
public interface ReportesDao {

	public List<LlenaComboEmpresasDto> consultarEmpresas(int usuario);
	public List<LlenaComboGralDto> consultarCajas(int usuario);
	public List<LlenaComboChequeraDto> consultarOrigen(String tipoMovto);
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, String sDivisa);
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa);
	public List<Map<String,Object>> consultarReporteCheques(Map<String, Object> datos);
	public List<Map<String, Object>> buscarDatosReporte(List<Map<String, String>> objParams);
	public List<Map<String, Object>> buscarDatosReportes(Map objParams);
	public List<Map<String, Object>> buscarTransferidas(Map objParams);
	public List<Map<String, Object>> entreCuentasEmp(Map objParams, String tipoOper);
}
