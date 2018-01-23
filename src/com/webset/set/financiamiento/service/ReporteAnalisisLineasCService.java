package com.webset.set.financiamiento.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.financiamiento.dto.AnalisisLineasCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ReporteAnalisisLineasCService {

	List<LlenaComboGralDto> obtenerGruposEmpresa();
	List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, boolean grupo, int noGrupo);
	List<LlenaComboGralDto> obtenerTipoFinanciamiento(String vsTipoMenu);
	List<AnalisisLineasCreditoDto> obtenerAnalisisLineas(int empresa, int tipoFinanciamiento, boolean vbTipoCambio,
			String vsMenu, String fechaInicio, String fechaFin);
	int obtenerValoresDivisa();
	List<AnalisisLineasCreditoDto> obtenerResumen(int empresa, int tipoFinanciamiento, String vsMenu);
	HSSFWorkbook excelAnalisisLineasCredito(String analisis);
	HSSFWorkbook excelAnalisisLineasCreditoResumen(String analisis); 

}
