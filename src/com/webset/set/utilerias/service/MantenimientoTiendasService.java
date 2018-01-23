package com.webset.set.utilerias.service;

import java.util.List;

import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoTiendasDto;


public interface MantenimientoTiendasService {
	public List<MantenimientoTiendasDto> llenaGridTiendas(String noAcredor);
	public String insertaMantenimientoTiendas(MantenimientoTiendasDto dto);
	public String updateMantenimientoTiendas(MantenimientoTiendasDto dto);
	public String deleteMantenimientoTiendas(MantenimientoTiendasDto dto);
	public List<LlenaComboGralDto> llenarComboBeneficiario(String ePersona,String nombre);
	public String exportaExcel(String datos);
}
