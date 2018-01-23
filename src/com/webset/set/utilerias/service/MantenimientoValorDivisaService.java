package com.webset.set.utilerias.service;

import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.MantenimientoValorDivisaDto;

public interface MantenimientoValorDivisaService {
	public List<MantenimientoValorDivisaDto> llenaComboTasas();
	public List<MantenimientoValorDivisaDto> llenaComboDivisas();
	public List<MantenimientoValorDivisaDto> llenaGridTasas(String fecha);
	public List<MantenimientoValorDivisaDto> llenaGridDivisas(String fecha);
	public String insertaActualiza(List<Map<String, String>> gridTasas, List<Map<String, String>> gridDivisas, String fecha);	
}
