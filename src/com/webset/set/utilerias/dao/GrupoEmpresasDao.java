package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.GrupoEmpresasDto;

public interface GrupoEmpresasDao {
	public List<GrupoEmpresasDto> llenaComboGrupo();
	public String configuraSet(int indice);
	public List<GrupoEmpresasDto> llenaComboEmpresa();
	public List<GrupoEmpresasDto> llenaGrid(int noEmpresa, int idGrupo, int todo);
	public List<GrupoEmpresasDto> buscaRegistro(int idGrupo, int noEmpresa);
	public int insertaRegistro(int idGrupo, int noEmpresa);
	public int eliminaRegistro(int idGrupo, int noEmpresa);
	public List<GrupoEmpresasDto> obtieneCorreo (int idGrupo);
	public int cambiaNivel(int idGrupo, int nivel);
	public List<Map<String, String>> reporteGrupoEmpresas();
}
