package com.webset.set.impresion.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.ChequeContinuoDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

/*
 * Luis Alfredo Serrato Montes de Oca
 */

public interface ConfiguracionContinuaDao {
	
	public List<ChequeContinuoDto> obtenerConfiguraciones();
	public List<LlenaComboGralDto> obtenerBancos();
	public List<LlenaComboChequeraDto> obtenerChequera(int idBanco, String idEmpresa);
	public String insertarConfiguracion(List<Map<String, String>> datos);
	public String eliminarConfiguracion(int idConfiguracion);
	public List<ConfiguracionChequeDto> obtenerCampos(int idConf);
	public String insertarCampos(List<Map<String, String>> datos);
	public String eliminarCampo(int idCampo);

}
