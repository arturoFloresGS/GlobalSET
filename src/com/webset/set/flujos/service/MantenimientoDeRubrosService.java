package com.webset.set.flujos.service;

import java.util.List;
import java.util.Map;
import com.webset.set.flujos.dto.MantenimientoDeRubrosDto;
import java.util.Map;
@SuppressWarnings("unchecked")
public interface MantenimientoDeRubrosService {
	public List<MantenimientoDeRubrosDto> llenarComboGrupo();
	public List<MantenimientoDeRubrosDto> consultarRubro(int idGrupo);
	public Map accionRubro(List<Map<String, String>> gListRubro);
	public Map eliminarRubro(int grupo, int rubro);
}
