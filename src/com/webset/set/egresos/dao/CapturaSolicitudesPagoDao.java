package com.webset.set.egresos.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.PolizaContableDto;
import com.webset.set.utilerias.dto.CajaUsuarioDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.LlenaFormaPagoDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;

import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;

public interface CapturaSolicitudesPagoDao {  //
	public Map<String, Object> insertaBitacoraPoliza(DT_Polizas_ResponseResponse dt_Polizas_ResponseResponse);
	public Map<String,Object> registrarPlantilla(Map<String, String> datIn);
	public List<String> fijaPlantilla(int idPlantilla, int idUsuario);
	public List<Boolean> habilitaComponetesPerfilEmpresa(String rubro, String poliza, String grupo);
	public List<LlenaComboEmpresasDto>obtenerEmpresas(int idUsuario);
	public List<LlenaComboDivisaDto> obtenerDivisas();
	public List<LlenaFormaPagoDto>obtenerFormaPago();
	public Date obtenerFechaHoy();
	public double obtenerTipoCambio(String idDivisa, Date fecha);
	public List<CajaUsuarioDto>obtenerCajaUsuario(int idUsuario);
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto);
	public List<PolizaContableDto>llenarComboPoliza(PolizaContableDto dto);
	public String consultarConfiguaraSet(int indice);
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto);
	public List<LlenaComboGralDto> obtenerBancoBenef(Map<String,Object> datos);
	public List<LlenaComboGralDto> obtenerChequeras(Map<String,Object> datos);
	public List<Map<String,Integer>> obtenerSucPlazClabe(Map<String, Object> datos);
	public List<MovimientoDto> consultarMovimientos(Map<String, Object> datos);
	public List<Map<String,Object>> obtenerDetalleMovimiento(int noFolioDet);
	public int obtenerPersona(String equivalePersona);
	public boolean verificarDocumento(int noDocto, int empresa);
	public int obtenerNoCuenta(int empresa);
	public int insertarParametro(ParametroDto dto);
	public String obtenerChequeraPagadora(String idDivisa);
	public int actualizarClabe(String folio, String clabe);
	public String obtenerSolicitante();
	public int actualizaPagoCompleto(String strNoFolioDet,
			String strImporteParcial, String strDescripcion);
	public List<String> obtenerGrupoDelRubro(String idRubro);
	public List<LlenaFormaPagoDto> llenarComboFormaPagoParametrizado( String poliza, String rubro, String Grupo);
	public String consultarConfiguraSet(int indice);
	public List<Map<String,String>> obtenerChequerasBancoInterlocutor(Map<String,Object> datos);
	public List<String> obtieneTransacionClasedocumento(String poliza, String Grupo , String rubro);
	public Map<String, Object> obtieneCheqPagadora(GeneradorDto generadorDto);
	List<LlenaComboGralDto> obtenerGrupo();
	public List<LlenaComboGralDto> obtenerRubro(LlenaComboGralDto dto);

}
