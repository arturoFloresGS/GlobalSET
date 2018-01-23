package com.webset.set.egresos.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.dto.PlantillaDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
/*
 * Interfaz Creada por Luis Alfredo Serrato Montes de Oca
 * 11092015
 */
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface PagosPedientesService {
	public List<PagosPendientesDto> llamarObtenerPagosPendientes(PlantillaDto plantilla, List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos);
	public List<PagosPendientesDto> llamarObtenerPagosPendientesNivDos(int numeroEmpresa, PlantillaDto plantilla,List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos);
	public List<PagosPendientesDto> llamarObtenerPagosPendientesNivTres(int numeroEmpresa, String acreedor, PlantillaDto plantilla , List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean documentos, String beneficiario);
	public String crearPropuestaPago(String matrizDatosPagos, int sociedad, double importeSeleccionado, String fechaPago, String divisa, String tipoBusqueda);
	public List<LlenaComboGralDto> obtenerListaBancos(String proveedor, String tipoBusqueda);
	public List<LlenaComboGralDto> obtenerListaChequeras(String proveedor, int idBanco, String tipoBusqueda);
	public List<LlenaComboGralDto> obtenerListaBancosPagador(int empresa, String divisa);
	public List<LlenaComboGralDto> obtenerListaChequerasPagadoras(int noEmpresa, String divisa, int idBanco, String beneficiario, String acreedor);
	public List<LlenaComboDivisaDto>obtenerDivisas();
	public PlantillaDto obtenerFechasIni();
	public String guardarPlantilla(String datosPlantilla, List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores);
	public List<PlantillaDto> obtenerListaPlantillas();
	public Map<String, Object> obtenerPlantilla(int idPlantilla);
	
	public List<LlenaComboGralDto> llenarComboReglaNegocio(LlenaComboGralDto dto);
	public String crearPropuestaPagoSimple(String matrizDatosPagos, int sociedad, double importeSeleccionado, String fechaPago, String divisa, String tipoBusqueda);
	public String controlFechas(String fechaHoy, String origen);
	
	public String pagosProgramados(String fecha);
	
	public List<LlenaComboGralDto> llenarComboGrupoFlujo();
	
	public List<PagosPendientesDto> obtenerTodosPagosPendientesPorEmpresa(int numeroEmpresa, String acredor, PlantillaDto plantilla ,String tipo);
	public String retornarJsonPendientesNivTres(int numeroEmpresa, String acreedor, PlantillaDto plantilla,
			List<Map<String, String>> rangosDocumentos, List<Map<String, String>> rangosProveedores, int marca, boolean documentos);
	public List<Map<String, String>> leerExcel(XSSFWorkbook workbook, String[] keys, int noUsuario, String tipo);
	public List<Map<String, String>> leerExcel(HSSFWorkbook workbook, String[] keys, int noUsuario, String tipo);
	public String ejecutarPagoParcial(String factura, float importeTotal, float importePagoParcial, String noFolioDet);
	public String ejecutarCambioDatosMovimiento(String fechaPago, String claves, int idBancoBenef, String idChequeraBenef, int idBancoPag, String idChequeraPag, String refBancaria, String tipoBusqueda);
	
}
