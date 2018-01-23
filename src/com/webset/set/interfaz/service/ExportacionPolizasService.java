package com.webset.set.interfaz.service;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface ExportacionPolizasService {
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto);
	public List<MovimientoDto> consultaPolizasExportar(String empresa, String origen, String fecInicio, String fecFin);
	public Map<String, Object> ejecutarExportacionPolizas (List<MovimientoDto> movimientos);
	
}
