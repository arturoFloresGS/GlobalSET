package com.webset.set.inversiones.middleware.service;

import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.dto.MantenimientoDePapelDto;

public interface MantenimientoDePapelService {
	public List<MantenimientoDePapelDto> consultarPapel();
	public List<MantenimientoDePapelDto> llenarComboTipoValor();
	public Map<String, Object> accionPapel(List<Map<String, String>> gListPapel, char bandera, String confir);
	public Map<String, Object> eliminarPapel(String papel);
}
