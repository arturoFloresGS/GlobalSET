package com.webset.set.inversiones.dao;

import java.util.List;

import com.webset.set.inversiones.dto.LlenaComboValoresDto;

/**
 * Interfase DAO para consultas comunes en el modulo de inversiones
 * @author COINSIDE
 *
 */
public interface InversionesComunDao {
	/**
	 * Consulta las instituciones
	 * @return valores generales para llenado de combo
	 */
	public List<LlenaComboValoresDto> consultarInstitucion();
	
	/**
	 * Consulta los tipos de valor
	 * @return valores generales para llenado de combo
	 */
	public List<LlenaComboValoresDto> consultarTipoValor();
	
	/**
	 * Consulta todas las divisas
	 * @return valores generales para llenado de combo
	 */
	public List<LlenaComboValoresDto> consultarDivisa();
}
