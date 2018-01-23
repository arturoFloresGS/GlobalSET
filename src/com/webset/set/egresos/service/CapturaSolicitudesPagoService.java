package com.webset.set.egresos.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.PolizaContableDto;
import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface CapturaSolicitudesPagoService {
	public List<String> fijaPlantilla(int idPlantilla, int idUsuario);
	public Map<String,Object> registrarPlantilla(Map<String, String> datIn);
	public List<Boolean> habilitaComponetesPerfilEmpresa (String empresa, String poliza, String grupo);
	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario);
	public List<LlenaComboDivisaDto>obtenerDivisas();
	public List<LlenaFormaPagoDto>obtenerFormaPago();
	public Date obtenerFechaHoy();
	public List<CajaUsuarioDto>obtenerCajaUsuario(int idUsuario);
	public List<LlenaComboGralDto>LlenarComboGral(LlenaComboGralDto dto);//
	public List<PolizaContableDto>llenarComboPoliza(PolizaContableDto dto);
	public String consultarConfiguraSet(int indice);
	public List<LlenaComboGralDto>llenarComboAreaDestino(LlenaComboGralDto dto);
	public List<LlenaComboGralDto>llenarComboBeneficiario(LlenaComboGralDto dto, int noEmpresa);
	public List<LlenaComboGralDto>obtenerBancoBenef(Map<String, Object> datos);
	public List<LlenaComboGralDto>obtenerChequeras(Map<String,Object> datos);
	public List<Map<String, Integer>> obtenerSucPlazClabe(Map<String,Object> datos);
	public List<MovimientoDto> consultarMovimientos(Map<String, Object> datos);
	public List<Map<String, Object>>obtenerDetalleMovimiento(int noFolioDet);
	public Map<String,Object> ejecutarSolicitud(Map<String, String> datIn);
		
	public Map<String, Double> obtenerCambioDivisa(String idDivisaOriginal, String idDivisaPago, double importeOriginal);
	public String obtenerSolicitante();
	
	public Map<String,Object> generarDocumento(Map<String, String> plantilla, Map<String, String> cabecera);
	public List<String> obtenerGrupoDelRubro(String idRubro);
	public List<LlenaFormaPagoDto>llenarComboFormaPagoParametrizado(String poliza, String rubro, String Grupo);
	public List<Map<String,String>> obtenerChequerasBancoInterlocutor(Map<String,Object> datos);
	public List<LlenaComboGralDto>llenarComboBeneficiarioParametrizado(LlenaComboGralDto dto, int noEmpresa, String grupo, String rubro);
	public Map<String, Object> obtieneCheqPagadora(int idEmpresa,String idDivOriginal,int idFormaPago);
	public List<LlenaComboGralDto> obtenerGrupo();
	public List<LlenaComboGralDto> obtenerRubro(LlenaComboGralDto dto);
	
	
	
	
}
