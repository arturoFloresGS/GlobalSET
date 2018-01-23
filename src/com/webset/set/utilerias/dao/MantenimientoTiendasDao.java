package com.webset.set.utilerias.dao;

import java.util.List;

import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoTiendasDto;


public interface MantenimientoTiendasDao {
	public List<MantenimientoTiendasDto> llenaGridTiendas(String noAcredor);
	public String insertaMantenimientoTiendas(MantenimientoTiendasDto dto);
	public String updateMantenimientoTiendas(MantenimientoTiendasDto dto);
	public String deleteMantenimientoTiendas(MantenimientoTiendasDto dto);
	public List<LlenaComboGralDto> llenarComboBeneficiario(String ePersona, String nombre);
}
