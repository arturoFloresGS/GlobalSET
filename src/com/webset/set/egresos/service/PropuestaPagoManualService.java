package com.webset.set.egresos.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.PropuestaPagoManualDto;
import com.webset.set.utilerias.dto.EstatusMovimientosDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface PropuestaPagoManualService {
	public Date obtenerFechaManana();
	public Date obtenerFechaHoy();
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto);
	public List<LlenaComboDivisaDto>llenarComboDivisa(String psDivisa);
	public String controlProyecto(int egreso, int bloqueo);
	public List<LlenaComboGralDto>llenarComboGrupo(Map<String,Object> datos);
	public List<LlenaComboGralDto>llenarComboBanco(Map<String,Object> datos);
	public List<LlenaComboGralDto>llenarComboChequera(Map<String,Object> datos);
	public List<LlenaComboGralDto>llenarCombo(LlenaComboGralDto dto);
	public List<PropuestaPagoManualDto> consultarPropuestas(Map<String, String> datos);
	public double sumarImportePropuestas(Map<String, String> datos);
	public List<EstatusMovimientosDto> verificarEstatusMovtos(String psFolios);
	public Map<String, Object> ejecutarPropuestas(List<PropuestaPagoManualDto> dto);
	
	public String muestraComponentes(int param);
	public List <PropuestaPagoManualDto> obtenerDivision(int usuario);
	public List <PropuestaPagoManualDto> obtenerDivisaAct(int empresa, String division);
	public List<PropuestaPagoManualDto> SelectBancoCheqBenef (String cliente, String Divisa, int banco);   
	public int UpdateBancoCheqBenef (String folio, int Banco, String Chequera);   	
	public String ConfiguraSet(int param, String cliente);
	public List<PropuestaPagoManualDto>consultarPropuestasAgregar(int noGrupoEmpresa);
	public String agregarPropuestas(int noGrupoEmpresa, String cveControl, String sFolios);
	public Map<String,Object> ejecutarSolicitud(Map<String, String> datIn, List<Map<String, String>> detalle);
	public List<PropuestaPagoManualDto> llenaComboGrupoConta(String idTipoMovto, String idSubGrupo);
	public List<PropuestaPagoManualDto> llenaComboSubGrupo(int tipoGrupo);
	public List<PropuestaPagoManualDto> llenaComboSubSubGrupo(int idRubroC);
	public String buscaCtaContable(String datosConta);
	public List<PropuestaPagoManualDto> llenarComboDepCCProy(int noEmpresa, int tipoCombo);
	public String modificaImporteApagar(int noFolioDet, double importe);
}
