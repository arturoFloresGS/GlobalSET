package com.webset.set.financiamiento.service;

import java.util.List;
import java.util.Map;
import com.webset.set.financiamiento.dto.GastoComisionCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface GastosFinanciamientoCService {
	
	public List<LlenaComboGralDto> obtenerContratos(String psTipoMenu, int iBanco, int noEmpresa);
	public List<LlenaComboGralDto> obtenerGastos();
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus);
	public List<GastoComisionCreditoDto> selectGastos(String linea, int disposicion);
	public Map<String, Object> insertAmort(String gastos);
	public Map<String, Object> eliminarGastos(String gastos);
	public List<Map<String, Object>> obtenerReporteGastos(String idLinea,int idDisposicion);
	
}