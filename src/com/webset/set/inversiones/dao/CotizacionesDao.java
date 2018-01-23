package com.webset.set.inversiones.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.inversiones.dto.ContratoInstitucionDto;
import com.webset.set.inversiones.dto.CotizacionDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;
import java.sql.Date;

/**
 * Interfase DAO para pantalla de Control de Inversiones
 * @author COINSIDE
 *
 */
public interface CotizacionesDao {
	/**
	 * Consulta los contactos de una institucion
	 * @return valores generales para llenado de combo
	 */
	public List<LlenaComboValoresDto> consultarContactosInst(int noInstitucion);
	
	/**
	 * Obtiene los contratos de una institucion
	 * @param noInstitucion
	 * @return
	 */
	public List <ContratoInstitucionDto> consultarContratos (int noInstitucion);
	
	/**
	 * Obtiene la divisa asociada a un Tipo Valor
	 * @param idTipoValor
	 * @return id de la divisa
	 */
	public String consultarDivisaTV(String idTipoValor);
	
	/**
	 * Valida si existe una cotizacion del plazo para un tipo valor de una institucion en la fecha dada
	 * @param plazo
	 * @param tipoValor
	 * @param idInstitucion
	 * @param fecha (dd/mm/yyyy)
	 * @return true si existe la cotizacion; false en otro caso
	 */
	public boolean validarCotizacion(int plazo, String tipoValor, int idInstitucion, Date fecha);
	
	/**
	 * Inserta la cotizacion de tasa
	 * @param cotizacion
	 */
	public void insertarCotizacion (CotizacionDto cotizacion);
	
	/**
	 * Obtiene los datos para generar el reporte de cotizaciones
	 * @param fecha
	 * @param noEmpresa
	 */
	public List<Map<String, Object>> consultarRepCotizaciones (String fecha, int noEmpresa);
	
}
