package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.EquivalenciaEmpresasDto;

public interface EquivalenciaEmpresasDao {
	public List<EquivalenciaEmpresasDto> llenaComboEmpresas();
	public List<EquivalenciaEmpresasDto> llenaGrid(String nomEmpresa);
	public int eliminaRegistro(String codigo, String empresaSet, String empresaInterface);
	public int actualizaEmpresa(List<Map<String, String>> registro);
	public List<EquivalenciaEmpresasDto> buscaRegistro(List<Map<String, String>> registro);
	public int insertaEmpresa(List<Map<String, String>> registro);
}
