package com.webset.set.inversiones.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.dto.MantenimientoDePapelDto;

public interface MantenimientoDePapelDao {
	public List<MantenimientoDePapelDto> consultarPapel();
	public List<MantenimientoDePapelDto> llenarComboTipoValor();
	public int eliminarPapel(String papel);
	public int validaNuevoPapel(List<Map<String, String>> gListPapel);
	public int nuevoPapel(List<Map<String, String>> gListPapel);
	public int modificarPapel(List<Map<String, String>> gListPapel, String confir);
}
