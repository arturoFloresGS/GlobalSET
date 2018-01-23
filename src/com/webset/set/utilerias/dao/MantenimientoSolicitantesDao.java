package com.webset.set.utilerias.dao;

import java.util.List;

import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesDto;


public interface MantenimientoSolicitantesDao {
	public List<MantenimientoSolicitantesDto> llenaGridSolicitantes(String nombre);
	public String insertSolicitante(MantenimientoSolicitantesDto dto);
	public String updateSolicitante(MantenimientoSolicitantesDto dto);
	public String deleteSolicitante(MantenimientoSolicitantesDto dto);
}
