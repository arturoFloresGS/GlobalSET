package com.webset.set.financiamiento.dao;

import java.util.List;

import com.webset.set.financiamiento.dto.AnalisisLineasCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ReporteAnalisisLineasCreditoDao {

	List<LlenaComboGralDto> obtenerGruposEmpresa();
	List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario);
	List<LlenaComboGralDto> obtenerTipoFinanciamiento(String vsTipoMenu);
	List<AnalisisLineasCreditoDto> obtenerAnalisisLineas(int empresa, int tipoFinanciamiento, boolean vbTipoCambio,
			String vsMenu, String fechaInicio, String fechaFin);
	List<AnalisisLineasCreditoDto> obtenerValoresDivisa();
	List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, int noGrupo);
	List<AnalisisLineasCreditoDto> obtenerResumen(int empresa, int tipoFinanciamiento, String vsMenu);

}
