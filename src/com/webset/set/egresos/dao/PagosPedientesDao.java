package com.webset.set.egresos.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.PagosPendientesDto;
import com.webset.set.egresos.dto.PlantillaDto;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface PagosPedientesDao {
	
	public List<PagosPendientesDto> obtenerPagosPendientes(PlantillaDto plantilla,List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos);
	public List<PagosPendientesDto> obtenerPagosPendientesNivDos(int numeroEmpresa, PlantillaDto plantilla,List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, boolean documentos);
	public List<PagosPendientesDto> obtenerPagosPendientesNivTres(int numeroEmpresa, String acredor, PlantillaDto plantillaList,List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores, boolean proveedores, String beneficiario);
	public int obtenerFolioCupos(int claveUsuario);
	public int insertarPropuestaPago(String claveControl, int sociedad, String fecha, String concepto);
	public int actualizaMvimiento(String folios, String chequera, int idBanco , String referenciaBanc , int idBancoP , String chequeraP ,String fecha, String claveControl, String tipoBusqueda);
	public List<LlenaComboGralDto> obtenerListaBancos(String proveedor, String tipoBusqueda);
	public List<LlenaComboGralDto> obtenerListaChequeras(String proveedor, int idBanco, String tipoBusqueda);
	public List<LlenaComboGralDto> obtenerListaBancosPagador(int noEmpresa, String divisa);
	public List<LlenaComboGralDto> obtenerListaChequerasPagadoras(int noEmpresa, String divisa, int idBanco, String beneficiario, String acreedor);
	public List<LlenaComboDivisaDto> obtenerDivisas();
	public PlantillaDto obtenerFechasIni();
	public String guardarPlantilla(PlantillaDto plantillas,int bandera);
	public String guardarRangos(List<Map<String, String>> rangosDocumentos ,List<Map<String, String>> rangosProveedores);
	public List<PlantillaDto> obtenerListaPlantillas();
	public PlantillaDto obtenerPlantilla(int idPlantilla);
	public List<Map<String,String>> obtenerPlantillaRangos(int idPlantilla);
	public boolean horaLimmite();
	public String existeConcepto(String concepto);
	public int agregarPropuestaPago(String claveControl, double d, String concepto);
	public List<LlenaComboGralDto>llenarComboReglaNegocio(LlenaComboGralDto dto);
	public int obtenerGrupoFlujo(int sociedad);
	public int obtenerDiaInhabil(String fecha);
	public boolean estaDisponible(String concepto, String claveControl,String fechaPropuesta);
	public Map<String, String> pagosProgramados(String fecha, String usuario);
	public List<LlenaComboGralDto> llenarComboGrupoFlujo(GrupoEmpresasDto dto);
	public List<PagosPendientesDto> obtenerTodosPagosPendientesPorEmpresa(int numeroEmpresa, String acredor, PlantillaDto plantilla , String tipo);
	public List<Map<String, String>> obtenerPagosPendientesNivTresJson(int numeroEmpresa, String acreedor,
	PlantillaDto plantilla, List<Map<String, String>> rangosDocumentos,
	List<Map<String, String>> rangosProveedores, int marca, boolean proveedores);
	int actualizaImporte(String claveControl);
	public void limpiarIndividuales(int noUsuario, String tipo);
	public void insertaIndividual(Map<String, String> datosExcel, int noUsuario, String tipo);
	public String ejecutarPagoParcial(String parametros);
	public int ejecutarCambioDatosMovimiento(String fechaPago, String claves, int idBancoBenef, String idChequeraBenef, int idBancoPag, String idChequeraPag, String refBancaria, String campoRefBancaria, String tipoBusqueda);
	public int claveControlValida(String claveControl);
	public String obtenProveedor(String[] folioDet);
	public String validaReferenciaCte(String noCliente);
	public void actualizaProveedor(String noCliente);
	int asignarChequeraPagadora(int idBancoPag, String idChequeraPag);
	
	
}
