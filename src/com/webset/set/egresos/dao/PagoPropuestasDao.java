package com.webset.set.egresos.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.BloqueoPagoCruzadoDto;
import com.webset.set.egresos.dto.PagosPropuestosDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.GrupoEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParamMovto3000Dto;
import com.webset.set.utilerias.dto.ParametroFactorajeDto;
import com.webset.set.utilerias.dto.SaldosChequerasDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

import mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos;
import mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse;

public interface PagoPropuestasDao {
	
	public List<LlenaComboGralDto> llenarComboGrupoEmpresa(GrupoEmpresasDto dto);
	public List<LlenaComboGralDto> llenarComboDivXEmp(int idUsuario);
	public List<SaldosChequerasDto> obtenerSaldosChequeras(SaldosChequerasDto dto);
	public Date obtenerFechaHoy();
	public double obtenerTipoCambio(String idDivisa, Date fecha);
	public int seleccionarFolioReal(String tipoFolio);
	public Map<String, Object> ejecutarPagador(StoreParamsComunDto dto) throws Exception;
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto);
	public Map<String, Object> ejecutarGenerador(int emp, int folParam,int folMovi, int folDeta, int result, String mensajeResp);
	public int actualizarSolicitudFactoraje(int noFolioDet);
	public List<MovimientoDto> consultarPagProp(PagosPropuestosDto dto);
	public Date consultarFechaHoy();
	public List<BloqueoPagoCruzadoDto> obtenerBloqueoProveedor(String noEmpresa, String noProv, String cveControl) ;
	public List<BloqueoPagoCruzadoDto> obtenerBloqueoSAP(String cveControl, String noDocto) ;
	public int existeChequeraBenef(MovimientoDto movimiento);
	public List<MovimientoDto>consultarDetalle(PagosPropuestosDto dto);
	public List<ComunEgresosDto> obtenerSumaMass(ComunEgresosDto dto);
	public int obtenerCtaMovsCpaVtaDiv(String cveControl);
	public String consultarConfiguraSet(int indice);
	public List<Map<String, String>> consultaPagosExcel(String cveControl);
	public List<MovimientoDto> consultarPagPropApagar(ComunEgresosDto dtoIn);
	public List<MovimientoDto> consultarPagosCpaVtaTransferPropManual(String cveControl);
	public List<MovimientoDto> consultarPagosCruzadosAut(ComunEgresosDto dto);
	public List<MovimientoDto> consultarPagosBenefDatosAlt(ComunEgresosDto dtoIn);
	public int contarMovsPropMAut(ComunEgresosDto dto);
	public List<MovimientoDto>consultarPagPropAut1(ComunEgresosDto dtoIn);
	public int actualizarBancoCheqBenef(int noFolioDet, int idBanco, String idChequera);
	public List<ComunDto> consultarBancoCheqBenef(ComunDto dto);
	public int actualizarBancoCheqBenef(ComunDto dto);
	public int actualizarEstatusComVtaTransfer(String psFolios, boolean bCiti);
	public List<String> consultarPagoMassPayment(int noFolioDet, Date fechaHoy);
	public int actualizarEstatusMass(String folios);
	public int insertarDato(int noEmpresa, String cadenaPagador);
	public int actualizarCveControl(int noEmpresa, String psFolios, String cveControl, Date fecPropuesta);
	public int insertarParametroFactoraje(ParametroFactorajeDto dto);
	public int actualizarMovto3000(ParamMovto3000Dto dto);
	public List<String> seleccionarBImpreContinua(int idBanco, String sChequera);
	public int ejecutarSpPagadorAgrupaCheques3000(int iIdUsuario, String sFolios, int iResult);
	
	public List<Map<String, Object>> reportePagoPropuestas(PagosPropuestosDto dtoIn);
	public List<Map<String, Object>> reportePagoPropuestasDetalle(PagosPropuestosDto dtoIn);
	public void actualizarFecPago(String cveControl, String fecHoy);
	public List<MovimientoDto> buscaDatosConfigContable(String cveControl);
	public List<MovimientoDto> buscaFolioDatosPagados(String cveControl, List<MovimientoDto> listDatPag, int i);
	public int actFolioPagConfigCont(int noFolioDetPag, int noFolioDet);
	public String obtieneOrigenMov(String cveControl);
	public int actualizaMovtoTC(int idUsuario, String fecModif, Double tipoCambio, String foliosDet,
			 String noEmpresa, String noDocto);
	public List<ComunEgresosDto> consultarPropAgrup(List<ComunEgresosDto> dtoIn);
	
	public DT_Pagos_OBPagos pagosParaWebservice (MovimientoDto folios, String fecHoy, int idUsuario);
	public int insertaBitacoraPagos (DT_Pagos_ResponsePagosResponse pagosRespuestaq);
	public int actualizaPagosCruzados (MovimientoDto folios);
	public int inserPagosZexpFact (MovimientoDto folios , int idUsuario);
	public int actualizaMovimientoCompensado(MovimientoDto folios, DT_Pagos_ResponsePagosResponse pagosRespuestaq,String fecHoy);
	public List<MovimientoDto>consultarDetallePropuestasNoPagadas(PagosPropuestosDto dto);
	public int verificaCompraTransfer(String cveControl);
	public boolean actualizaCompraTransfer(String cveControl);
	public boolean ejecutaCVT(String cveControl);
}
