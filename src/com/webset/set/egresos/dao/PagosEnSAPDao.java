package com.webset.set.egresos.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.ComunEgresosDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParamMovto3000Dto;
import com.webset.set.utilerias.dto.ParametroFactorajeDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

import mx.com.gruposalinas.Pagos.DT_Pagos_OBPagos;
import mx.com.gruposalinas.Pagos.DT_Pagos_ResponsePagosResponse;

public interface PagosEnSAPDao {
	
	public List<MovimientoDto> consultarPagPropApagar(ComunEgresosDto dtoIn);
	public List<MovimientoDto> consultarPagosCpaVtaTransferPropManual(String cveControl);
	public List<MovimientoDto> consultarPagosCruzadosAut(ComunEgresosDto dto);
	public List<MovimientoDto> consultarPagosBenefDatosAlt(ComunEgresosDto dtoIn);
	public int contarMovsPropMAut(ComunEgresosDto dto);
	public int actualizarBancoCheqBenef(ComunDto dto);
	public int actualizarEstatusComVtaTransfer(String psFolios, boolean bCiti);
	public List<String> consultarPagoMassPayment(int noFolioDet, Date fechaHoy);
	public int actualizaMovtoTC(int idUsuario, String fecModif, Double tipoCambio, String foliosDet,
			 String noEmpresa, String noDocto);
	public Map<String, Object> cambiarCuadrantes(StoreParamsComunDto dto);
	public List<ComunDto> consultarBancoCheqBenef(ComunDto dto);
	public List<MovimientoDto>consultarPagPropAut1(ComunEgresosDto dtoIn);
	public String consultarConfiguraSet(int indice);
	public Date obtenerFechaHoy();
	public int actualizarEstatusMass(String folios);
	public String obtieneOrigenMov(String cveControl);
	public int actualizarBancoCheqBenef(int noFolioDet, int idBanco, String idChequera);
	public DT_Pagos_OBPagos pagosParaWebservice (MovimientoDto folios, boolean bFactoraje, int factoraje);
	public int insertaBitacoraPagos (DT_Pagos_ResponsePagosResponse pagosRespuestaq);
	public int actualizaPagosCruzados (MovimientoDto folios);
	public int inserPagosZexpFact (MovimientoDto folios , int idUsuario);
	public int actualizaMovimientoCompensado(MovimientoDto folios, DT_Pagos_ResponsePagosResponse pagosRespuestaq,String fecHoy);
	public void actualizarFecPago(String cveControl, String fecHoy);
	public int insertarDato(int noEmpresa, String cadenaPagador);
	public int actualizarCveControl(int noEmpresa, String psFolios, String cveControl, Date fecPropuesta);
	public int insertarParametroFactoraje(ParametroFactorajeDto dto);
	public int actualizarMovto3000(ParamMovto3000Dto dto);
	public Map<String, Object> ejecutarPagador(StoreParamsComunDto dto);
	public Map<String, Object> ejecutarGenerador(int emp, int folParam,int folMovi, int folDeta, int result, String mensajeResp);
	public int actualizarSolicitudFactoraje(int noFolioDet);
	public int seleccionarFolioReal(String tipoFolio);
	public int updateSesionOperacionesCompraDeTransferParaPago() ;
	public int updateCancelSesionOperacionesCompraDeTransferParaPago( String folios );
	public void actualizaMovimientoCompensar(List<ComunEgresosDto> listGrid);
	public void actualizaMovimientoANoCompensar(List<ComunEgresosDto> listGrid);
	public int actualizaEjecucionOperacionesDivisas( int noUsuario, String folios);
}
