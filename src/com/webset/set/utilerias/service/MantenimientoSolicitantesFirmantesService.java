package com.webset.set.utilerias.service;

import java.util.List;

import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;


public interface MantenimientoSolicitantesFirmantesService {
	public List<MantenimientoSolicitantesFirmantesDto> llenaGridSolicitantesFirmantes(String tipoPersona);
	public String insertaMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto);
	public String updateMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto);
	public String deleteMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto);
	public String exportaExcel(String datos);
	public List<LlenaComboGralDto> llenarComboPersonas();
}
