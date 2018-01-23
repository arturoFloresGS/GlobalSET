package com.webset.set.flujos.dao;

import java.util.List;
import java.util.Map;
import com.webset.set.flujos.dto.MantenimientoDeRubrosDto;
import java.util.Map;


@SuppressWarnings("unchecked")
public interface MantenimientoDeRubrosDao {
	public List<MantenimientoDeRubrosDto> llenarComboGrupo();
	public List<MantenimientoDeRubrosDto> consultarRubro(int idGrupo);
	public int accionRubro(List<Map<String, String>> gListRubro);
	public int eliminarRubro(int grupo, int rubro);

}
