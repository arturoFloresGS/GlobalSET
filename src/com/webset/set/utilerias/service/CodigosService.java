package com.webset.set.utilerias.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.traspasos.dto.GrupoDTO;
import com.webset.set.traspasos.dto.RubroDTO;
import com.webset.set.utileriasmod.dto.CodigosDto;

public interface CodigosService {
	public List<CodigosDto> llenaComboEmpresas();
	public List<CodigosDto> llenaGrid(int noEmpresa);
	public String eliminaRegistro(int idRenglin, int idRubro ,String  opcion);
	public String insertaCodigo(String idCodigo, String descCodigo, int noEmpresa);
	public String insertaGrupo(String idCodigo, String descCodigo);
	public List<RubroDTO> getRubros(int idGrupo);
	public String insertaRubro(String idGrupo, String idRubro,
			String descRubro, String ingresoEgreso);
	public List<RubroDTO> getGuiasContables(int noEmpresa);
	public String insertaGuiaContable(String noEmpresa, String idGrupo,
			String idRubro, String cuentaContable);
	
	/*
	 * Luis Alfredo Serrato Montes de Oca
	 */
	public List<CodigosDto> getPolizas();
	public String agregarPoliza(int idPoliza, String nombrePoliza);
	public String eliminarPoliza(int idPoliza);
	public List<GrupoDTO> llenarComboGrupo(String idTipoGrupo);
	public List<CodigosDto> obtenerPolizasSinAsignar(String idRubro);
	public String asignarPolizas(String json, String idRubro);
	public List<CodigosDto> obtenerPolizasAsignadas(String idRubro);
	public String eliminarPolizas (String json, String idRubro);
	public HSSFWorkbook reporteGrupos();
	public HSSFWorkbook reporteGrupoPolizas();
	public HSSFWorkbook reporteGrupoRubros();
		
}
