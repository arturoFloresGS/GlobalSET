package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoCatalogosDto;

public interface MantenimientoCatalogosDao {
	public List<MantenimientoCatalogosDto> llenaComboCatalogos();
	public List<Map<String, Object>> llenaGrid(String nombreCatalogo,String noEmpresa,String noBanco,String idChequera, String[] vecColumnas, String orden);
	public String addRecord(String catalogo, String key, String tipo);
	public int deleteRecord(String catalogo, String key,String valor);
	public int saveRecord(String catalogo,
			List<Map<String, String>> matRegistros,
			List<Map<String, String>> defCols);
	public List<Map<String, String>> reporteCatalogo(String catalogo, String campos, String[] keys, String orden);
	public Map<String, String> getColumnas(String catalogo);
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco, int noEmpresa);
}
