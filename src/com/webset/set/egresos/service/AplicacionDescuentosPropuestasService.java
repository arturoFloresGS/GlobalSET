package com.webset.set.egresos.service;

/**
 * 20/01/2016
 * @author Luis Alfredo Serrato Montes de Oca
 **/

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface AplicacionDescuentosPropuestasService {

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
	 * @param String json con los parametros de busqueda
	 * @return lista PagosPendientesDto
	 */
	public List<PagosPendientesDto> obtenerPagos(String json);
	
	/**
	 * Metodo para llenar el grid de detalles propuestas facturas
	 * @author Luis Serrato
	 * @param noEmpresa 
	 * @param lista de mapas con los parametros de busqueda
	 * @return String Clave de la propuesta
	 */
	
	public List<PagosPendientesDto> obtenerDescuentos(List<String> listDes, String noEmpresa);
	
	public String aplicarDescuentoSimple(String json, String cvePago, List<String> listD, List<String> listPag);
	
	public List<PagosPendientesDto> obtenerDetalleDesc(List<String> clavesD, String ePersona, String numeroEmpresa);

	public List<PagosPendientesDto> obtenerDetallePagos(List<String> listDet, List<String> listD);
	
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);

	public List<LlenaComboGralDto> consultarProveedores(String texto);

	public HSSFWorkbook consultaPropuestasDescuentos(String claveP, String claveD);

	public HSSFWorkbook consultaHeader(String claveP, String claveD);
	 
	
}
