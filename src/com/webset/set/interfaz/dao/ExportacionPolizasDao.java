package com.webset.set.interfaz.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;

public interface ExportacionPolizasDao {
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto);
	public List<MovimientoDto> consultaPolizasExportar(String empresa, String origen, String fecInicio, String fecFin);
	public String consultarConfiguaraSet(int indice);
	public Map<String, Object> insertaBitacoraPoliza(DT_Polizas_ResponseResponse[] dt_Polizas_ResponseResponse, List<MovimientoDto> movimientos);
	public Map<String, Object> insertaZoperaciones(List<MovimientoDto> movimientos, String idUsuario);
	public void eliminaPolizasZoperaciones(List<MovimientoDto> movimientos);
}
