package com.webset.set.caja.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.dto.CuadrantesDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public interface CajaDao {

	public List<LlenaComboEmpresasDto> consultarEmpresas(int idUsuario);
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa);
	public List<LlenaComboDivisaDto> consultarDivisas(int cnt);
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa);
	public List<MovimientoDto> consultarChequesPorEntregar(ConsultaChequesDto dtoBus);
	public int actualizarEstatusEfvo(String sFolios);
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizaFolioCaja(int iNoRecibo, int iFolioDet);
	@SuppressWarnings("unchecked")
	public Map ejecutaCuadranteNetroSet(CuadrantesDto dto);
	public List<LlenaComboGralDto> consultarBancosVentana(int iEmpresa);
	public List<LlenaComboChequeraDto> consultarChequerasVentana(int iBanco, int iEmpresa);
	public List<Map<String,Object>> consultarReporteChequesPorEntregar(Map<String, Object> datos);
	public List<Map<String,Object>> consultarReporteChequesEntregados(Map<String, Object> datos);
	public List<Map<String,Object>> consultarReporteHistoricoChequesEntregados(Map<String, Object> datos);
	public List<Map<String,Object>> consultarReporteChequesEntregadosPorCaja(Map<String, Object> datos);
	public List<Map<String,Object>> consultarReporteHistoricoChequesEntregadosPorCaja(Map<String, Object> datos);
	public Map ejecutarPagador(StoreParamsComunDto dto);
	public void actualizarFecPago(String cveControl, String fecHoy);
}
