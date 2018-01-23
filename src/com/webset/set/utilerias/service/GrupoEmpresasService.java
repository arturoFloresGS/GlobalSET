package com.webset.set.utilerias.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.utileriasmod.dto.GrupoEmpresasDto;

public interface GrupoEmpresasService {
	public List<GrupoEmpresasDto> llenaComboGrupo();
	public String configuraSet(int indice);
	public List<GrupoEmpresasDto> llenaComboEmpresa();
	public List<GrupoEmpresasDto> llenaGrid(int noEmpresa, int idGrupo, int todo);
	public String insertaRegistro(List<Map<String, String>> registro);
	public int eliminaRegistro(int idGrupo, int noEmpresa);
	public GrupoEmpresasDto obtieneCorreo(int idGrupo);
	public String cambiaNivel(int idGrupo, int nivel);
	public HSSFWorkbook reporteGrupoEmpresas();
}
