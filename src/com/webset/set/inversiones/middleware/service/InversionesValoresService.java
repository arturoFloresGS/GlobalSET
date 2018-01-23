package com.webset.set.inversiones.middleware.service;

import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import com.webset.set.inversiones.dto.MantenimientoValoresDto;

public interface InversionesValoresService {
	public List<MantenimientoValoresDto> consultarValores();
	public List<LlenaComboValoresDto> consultarDivisa();
	public Map<String, Object> insertarModificarValores(boolean bNuevo, boolean bModifi, List< MantenimientoValoresDto> listVal);
	public Map<String, Object> eliminarValores(String sIdvalor);
	
}
