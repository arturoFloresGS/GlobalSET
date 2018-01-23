package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.MantenimientoConceptosDto;

public interface MantenimientoConceptosDao {
	public List<MantenimientoConceptosDto> llenaBancos();		
	public List<MantenimientoConceptosDto> llenaFormaPago(String ingresoEgreso);
	public List<MantenimientoConceptosDto> llenaGrid(int idBanco, String conceptoBanco, String formaPago, String ingresoEgreso);
	public int actualizaConcepto(MantenimientoConceptosDto obj);
	public List<MantenimientoConceptosDto> buscaConcepto(MantenimientoConceptosDto obj);
	public List<MantenimientoConceptosDto> buscaFormaPago(MantenimientoConceptosDto obj);
 	public int insertaConcepto(MantenimientoConceptosDto obj);
 	public int eliminaConcepto(int idBanco, String conceptoBanco, String ingresoEgreso);
 	public List<MantenimientoConceptosDto> llenaTipoOperacion();
	public List<Map<String, Object>> ejecutarReporteConcepto(Map<String, Object> parameters);
}
