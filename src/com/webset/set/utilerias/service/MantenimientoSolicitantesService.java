package com.webset.set.utilerias.service;

import java.util.List;

import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesDto;

public interface MantenimientoSolicitantesService {
	public List<MantenimientoSolicitantesDto> llenaGridSolicitantes(String nombre);
	public String insertSolicitante(MantenimientoSolicitantesDto dto);
	public String updateSolicitante(MantenimientoSolicitantesDto dto);
	public String deleteSolicitante(MantenimientoSolicitantesDto dto);
	public String exportaExcel(String datos);
}
