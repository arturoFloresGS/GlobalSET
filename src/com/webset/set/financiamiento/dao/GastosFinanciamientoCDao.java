package com.webset.set.financiamiento.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.GastoComisionCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface GastosFinanciamientoCDao {
	public List<LlenaComboGralDto> obtenerContratos(String psTipoMenu, int iBanco, int noEmpresa);
	public List<LlenaComboGralDto> obtenerGastos();
	public List<LlenaComboGralDto> obtenerDisposiciones(String linea, boolean estatus);
	public List<GastoComisionCreditoDto> selectGastos(String linea, int disposicion);
	public int insertAmort(AmortizacionCreditoDto amortizacionCreditoDto, String piBisiesto);
	public int eliminarGastos(AmortizacionCreditoDto amortizacionCreditoDto);
	public List<Map<String, Object>> obtenerReporteGastos(String idLinea,int idDisposicion);
	public  int obtenerIdAmortizacion(String linea, int idDisposicion);
}
