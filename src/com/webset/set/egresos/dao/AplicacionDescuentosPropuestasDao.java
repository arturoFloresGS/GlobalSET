package com.webset.set.egresos.dao;

/**
 * 20/01/2016
 * @author Luiz Luis Alfredo Serrato Montes de Oca
 **/
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;



public interface AplicacionDescuentosPropuestasDao {
	
	/**
	 * Metodo para llenar el combo de grupo de empresas
	 * @author Luis Serrato
	 * @param String clave del usuario conectado
	 * @return lista LlenaComboGralDto
	 */
	public List<LlenaComboGralDto> llenarComboGrpEmpresas(String idUsuario);
	/**
	 * Metodo para llenar el grid de propuestas facturas
	 * @author Luis Serrato
	 * @param lista de mapas con los parametros de busqueda
	 * @return lista PagosPendientesDto
	 */
	public List<PagosPendientesDto> obtenerPagos(List<Map<String, String>> datos);
	/**
	 * Metodo para llenar el grid de detalles propuestas facturas
	 * @author Luis Serrato
	 * @param lista de mapas con los parametros de busqueda
	 * @return String Clave de la propuesta
	 */
	public List<PagosPendientesDto> obtenerDetallePagos(String cveControl, String cveControlD);
	
	public List<PagosPendientesDto> obtenerDescuentos(String cveControl, String noEmpresa);
	
	public int modificarMovimiento(String foliDet, String cvePago, String cveDescueto); 
	
	public int modificarPopuestaPago(String cveDesc, String cvePago, String folios);
	
	public List<PagosPendientesDto> obtenerDetalleDesc(String cveControl, String ePersona, String numeroEmpresa);
	
	public int actualizarMovimiento(String clavesPagos, String clavePago);
	
	public int eliminarSeleccionAut(String clavesPagos);
	
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);
	
	public List<LlenaComboGralDto> consultarProveedores(String texto);
	
	public List<Map<String, String>> consultaMovimientos(String claveP, String claveD);
	
	public List<Map<String, String>> consultaHeader(String claveP, String claveD);
	
	public int modificarPropuestaDescuento(String string, String folios);

}
