package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utileriasmod.dto.CodigosDto;

public interface CodigosDao {
	public List<CodigosDto> llenaComboEmpresas();
	public List<CodigosDto> llenaGrid(int noEmpresa);
	public int eliminaCodigo(int idRenglon, int idRubro, String codigo);
	public List<CodigosDto> buscaCodigo(int noEmpresa, String idCodigo);
	public int insertaCodigo (int noEmpresa, String idCodigo, String descCodigo);
	public int buscaGrupo(String idGrupo);
	public int insertaGrupo(String idGrupo, String descGrupo);
	public List<RubroDTO> getRubros(int idGrupo);
	public int buscaRubro(String idGrupo, String idRubro);
	public int insertaRubro(String idGrupo, String idRubro, String descRubro,
			String ingresoEgreso);
	public List<RubroDTO> getGuiasContables(int noEmpresa);
	public int buscaGuiaContable(String noEmpresa, String idGrupo,
			String idRubro, String cuentaContable);
	public int insertaGuiaContable(String noEmpresa, String idGrupo,
			String idRubro, String cuentaContable);
	public int modificarGrupo(String idGrupo, String descGrupo);
	public int modificarRubro(String idGrupo, String idRubro, String descRubro,String ingresoEgreso);
	
	/*
	 * Luis Alfredo Serrato Montes de Oca
	 */
	public List<CodigosDto> getPolizas();
	public String agregarPoliza(int idPoliza, String nombrePoliza);
	public int existePoliza(int idPoliza, String descPoliza);
	public String actualizarPoliza(int idPoliza, String nombrePoliza);
	public String eliminarPoliza(int idPoliza);
	//public int insertarGrupoPoliza(String idGrupo);
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo);
	public List<CodigosDto> obtenerPolizasSinAsignar(String idRubro);
	public String asignarPolizas(String idPoliza, String idRubro);
	public List<CodigosDto> obtenerPolizasAsignadas(String idRubro);
	public String eliminarPolizas (String json, String idRubro);
	public List<Map<String, String>> reporteGrupos();
	public List<Map<String, String>> reporteGrupoPolizas();
	public List<Map<String, String>> reporteGrupoRubros();

}
