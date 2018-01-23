package com.webset.set.caja.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import com.webset.set.impresion.dto.ConsultaChequesDto;
import com.webset.set.traspasos.dto.MovimientoDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface CajaService {

	public List<LlenaComboEmpresasDto> llenarComboEmpresas(int idUsuario);
	public List<LlenaComboGralDto> llenarComboBancos(int iEmpresa);
	public List<LlenaComboDivisaDto> llenarComboDivisas(int cnt);
	public List<LlenaComboChequeraDto> llenarComboChequeras(int iBanco, int iEmpresa);
	public List<MovimientoDto> llenarGridChequesPorEntregar(ConsultaChequesDto dtoBus);
	public Map<String, Object> ejecutarEntregaCheques(List<MovimientoDto> listCheques, String fecHoy, int usuario);
	public List<LlenaComboGralDto> llenarComboBancosVentana(int iEmpresa);
	public List<LlenaComboChequeraDto> llenarChequerasVentana(int iBanco, int iEmpresa);
	@SuppressWarnings("unchecked")
	public JRDataSource llenarReporteCheques(String nomReporte, Map parameters);
	public List<Map<String, Object>> llenarReporteChequesPorEntregar(Map<String, Object> datos);
}
