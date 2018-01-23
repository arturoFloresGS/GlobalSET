package com.webset.set.utilerias.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoCatalogosDto;

public interface MantenimientoCatalogosService {
	public List<MantenimientoCatalogosDto> llenaComboCatalogos();
	public List<Map<String, Object>> llenaGridCatalogos(String nombreCatalogo,String noEmpresa,String noBanco,String idChequera);
	public String addRecord(String catalogo,String key,String tipo);
	public MantenimientoCatalogosDto getBodyReport();
	public int deleteRecord(String catalogo, String key,String valor);
	public int saveRecord(String catalogo,
			List<Map<String, String>> matRegistros,
			List<Map<String, String>> defCols);
	public HSSFWorkbook reporteCatalogo(String catalogo);
	
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco, int noEmpresa);
	
}
