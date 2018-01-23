package com.webset.set.impresion.service;

import java.util.List;

import com.webset.set.impresion.dto.ChequeContinuoDto;
import com.webset.set.impresion.dto.ConfiguracionChequeDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
/*
 * Luis Alfredo Serrato Montes de Oca
 */

public interface ConfiguracionContinuaService {

	public List<ChequeContinuoDto> obtenerConfiguraciones();
	public List<LlenaComboGralDto> obtenerBancos();
	public List<LlenaComboChequeraDto> obtenerChequera(int idBanco, String idEmpresas);
	public String insertarConfiguracion(String json);
	public String eliminarConfiguracion(int idConfiguracion);
	public List<ConfiguracionChequeDto> obtenerCampos(int idConf);
	public List<LlenaComboGralDto> obtenerFuentes();
	public String insertarCampos(String json);
	public String eliminarCampo(int idCampo);
}
