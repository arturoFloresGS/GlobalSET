package com.webset.set.impresion.service;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface MantenimientosService {
	//Patanlla de Mantenimiento de Firmantes
	public List<LlenaComboGralDto> llenarComboNoFirmantes();
	public List<MantenimientosDto> buscaFirmantes();
	public Map<String, Object> eliminarFirmantes(List<MantenimientosDto> list);
	public Map<String, Object> insertarFirmantes(List<MantenimientosDto> list);
	
	//Patanlla de Mantenimiento de Firmas
	public List<LlenaComboGralDto> llenaComboBancos();
	public List<LlenaComboGralDto> llenaComboChequeras(int idBanco);
	public List<LlenaComboGralDto> llenaComboPersonas(String idBanco , String cuenta, boolean busqueda);
	public List<MantenimientosDto> buscaFirmas(String idBanco, String idChequera, String noFirma);
	public String eliminarFirma(MantenimientosDto dto);
	public String insetarFirma(MantenimientosDto dto);
	
	public String exportaExcel(String datos,String op);
}
