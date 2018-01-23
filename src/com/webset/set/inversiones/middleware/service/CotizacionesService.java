package com.webset.set.inversiones.middleware.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.dto.ContratoInstitucionDto;
import com.webset.set.inversiones.dto.CotizacionesDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;

/**
 * Interface de servicios para Control de Cotizaciones
 * @author COINSIDE
 *
 */
public interface CotizacionesService 
{
	public List<LlenaComboValoresDto> consultarContactosInst(int noInstitucion);
	
	/**
	 * Obtiene los contratos de una institucion
	 * @param noInstitucion
	 * @return
	 */
	public List<ContratoInstitucionDto> consultarContratos(int noInstitucion);
	
	/**
	 * Obtiene la divisa del tipo valor dado
	 * @param idTipoValor
	 * @return
	 */
	public String consultarDivisaTV(String idTipoValor);
	
	/**
	 * Registra los valores de las cotizaciones
	 * @param cotizacionesDto
	 */
	public String registrarCotizaciones(CotizacionesDto cotizacionesDto);
	
	/**
	 * Obtiene una lista de mapas con la informacion para el reporte de cotizaciones
	 * @param fecha
	 * @param noEmpresa
	 * @return
	 */
	public List<Map<String, Object>> obtenerRepCotizaciones (String fecha, int noEmpresa);
}
