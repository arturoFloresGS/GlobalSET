package com.webset.set.inversiones.middleware.service;

import java.util.List;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;

/**
 * Interface de servicios comunes para el modulo de inversiones
 * @author COINSIDE
 *
 */
public interface InversionesComunService 
{
	public List<LlenaComboValoresDto> consultarInstitucion();
	public List<LlenaComboValoresDto> consultarTipoValor();
	public List<LlenaComboValoresDto> consultarDivisas();
}
