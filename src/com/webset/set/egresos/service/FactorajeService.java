package com.webset.set.egresos.service;

import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface FactorajeService {

	public List<PagosPendientesDto> obtenerListaFactoraje(int empresa, int proveedor, String fechaIni, String fechaFin);
	public List<LlenaComboGralDto> obtenerProveedores(String filtro);
	public List<LlenaComboGralDto> obtenerIntermediarios();
	//public String enviarDatos(String json, int noFactoraje, String fechaFactoraje);
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);
	public Map<String, Object> enviarDatos(String json, int noFactorae, String fechaFactoraje) ;
}
