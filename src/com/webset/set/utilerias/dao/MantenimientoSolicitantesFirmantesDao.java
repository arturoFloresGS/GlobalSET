package com.webset.set.utilerias.dao;

import java.util.List;

import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;


public interface MantenimientoSolicitantesFirmantesDao {
	public List<MantenimientoSolicitantesFirmantesDto> llenaGridSolicitantesFirmantes(String tipoPersona);
	public String insertaMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto);
	public String updateMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto);
	public String deleteMantenimientoSolicitantesFirmantes(MantenimientoSolicitantesFirmantesDto dto);
	
	public List<LlenaComboGralDto> llenarComboPersonas();
}
