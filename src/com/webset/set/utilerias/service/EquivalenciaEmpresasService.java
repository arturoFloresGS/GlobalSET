package com.webset.set.utilerias.service;

import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.EquivalenciaEmpresasDto;

public interface EquivalenciaEmpresasService {
	public List<EquivalenciaEmpresasDto> llenaComboEmpresas ();
	public List<EquivalenciaEmpresasDto> llenaGrid(String nomEmpresa);
	public String validaDatos(List<Map<String, String>> registro);
	public int eliminaRegistro(String codigo, String empresaSet, String empresaInterface);
	public String insertaActualizaEmpresa(List<Map<String, String>> grid);
	public String exportaExcel(String datos);
}
