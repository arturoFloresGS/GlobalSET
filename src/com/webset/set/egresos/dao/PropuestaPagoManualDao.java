package com.webset.set.egresos.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.PropuestaPagoManualDto;
import com.webset.set.utilerias.dto.EstatusMovimientosDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboDivisaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;


public interface PropuestaPagoManualDao {
	public String consultarConfiguraSet(int indice);
	public List<LlenaComboGralDto>llenarComboGral(LlenaComboGralDto dto);
	public Date obtenerFechaManana();
	public Date obtenerFechaHoy();
	public int obtenerDiasDiferencia(Date fecha1, Date fecha2);
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto);
	public List<LlenaComboDivisaDto>llenarComboDivisa(String psDivisa);
	public String controlProyecto(int egreso, int bloqueo);
	public List<LlenaComboGralDto> llenarComboGrupo(Map<String,Object> datos);
	public List<PropuestaPagoManualDto> consultarPropuestaPagoManual(Map<String, String> datos);
	public double sumarImportePropuestas(Map<String, String> datos);
	public String filtroCriterios(Map<String, String> datos);
	public List<LlenaComboGralDto> llenarComboBanco(Map<String,Object> datos);
	public List<LlenaComboGralDto> llenarComboChequera(Map<String,Object> datos);
	public List<EstatusMovimientosDto> verificarEstatusMovtos(String psFolios);
	public int seleccionarFolioCupos(int usuario);
	public int actualizarFolioCupos(int usuario);
	public int crearCupoManual(PropuestaPagoManualDto dtoInsert, String psDivision);
	public int actualizarFechaProp(PropuestaPagoManualDto dtoUpdate);
	
	public String muestraComponentes(int param);
	public List <PropuestaPagoManualDto> obtenerDivision(int usuario);
	public List <PropuestaPagoManualDto> obtenerDivisaAct(int empresa, String division);
	public List<PropuestaPagoManualDto> SelectBancoCheqBenef (String cliente, String Divisa, int banco);   
	public int UpdateBancoCheqBenef (String folio, int Banco, String Chequera);
	public List<LlenaComboGralDto> llenarComboChequeraBenef(Map<String,Object> datos);
	public List<PropuestaPagoManualDto> consultarPropuestasAgregar(int noGrupoEmpresa);
	public List<PropuestaPagoManualDto> selectPropuestaAcutal(int noGrupoEmpresa, String cveControl);
	public int actualizarPropuesta(String cveControl, double importe, int noGrupoEmpresa);
	public List<PropuestaPagoManualDto> buscaMovimientos(String sFolios);
	public int actualizarMovimientos(String cveControl, int noFolioMov, Date fecPropuesta, int idBanco, String idChequera);
	public List<PropuestaPagoManualDto> buscaBancoCheqPagadoras(String cveControl);
	public int obtenerNoCuenta(int empresa);
	public String consultarConfiguaraSet(int indice);
	public boolean verificarDocumento(int noDocto, int empresa);
	public int actualizarFolioReal(String tipoFolio);
	public int seleccionarFolioReal(String tipoFolio);
	public String obtenerChequeraPagadora(String idDivisa);
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto);
	public int actualizaPagoCompleto(String strNoFolioDet,
			double ImporteParcial, String strDescripcion);
	public int actualizaPagoCompleto3001(String strNoFolioDet,
			double ImporteParcial, String strDescripcion);
	public int insertarParametro(ParametroDto dto);
	public int actualizarClabe(String folio, String clabe);
	public List<PropuestaPagoManualDto> llenaComboGrupoConta(String idTipoMovto, String idSubGrupo);
	public List<PropuestaPagoManualDto> llenaComboSubGrupo(int tipoGrupo);
	public List<PropuestaPagoManualDto> llenaComboSubSubGrupo(int idRubroC);
	public String buscaCtaContable(String datosConta);
	public List<PropuestaPagoManualDto> llenarComboDepto(int noEmpresa);
	public List<PropuestaPagoManualDto> llenarComboCCosto(int noEmpresa);
	public List<PropuestaPagoManualDto> llenarComboProyecto(int noEmpresa);
	public int modificaImporteApagar(int noFolioDet, double importe);

}
