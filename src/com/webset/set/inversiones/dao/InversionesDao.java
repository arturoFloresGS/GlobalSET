package com.webset.set.inversiones.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.inversiones.dto.BancoCheContratoDto;
import com.webset.set.inversiones.dto.ComunInversionesDto;
import com.webset.set.inversiones.dto.ConsultaOrdenInversionDto;
import com.webset.set.inversiones.dto.CtasContratoDto;
import com.webset.set.inversiones.dto.CuentaPropiaDto;
import com.webset.set.inversiones.dto.MovimientoDto;
import com.webset.set.inversiones.dto.OrdenInversionDto;
import com.webset.set.inversiones.dto.ParamReportesDto;
import com.webset.set.inversiones.dto.ParametroDto;
import com.webset.set.inversiones.dto.RetencionDto;
import com.webset.set.inversiones.dto.RubroDto;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.CatPapelDto;
import com.webset.set.utilerias.dto.CatTipoValorDto;
import com.webset.set.utilerias.dto.CuentaDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.RevividorDto;

import mx.com.gruposalinas.Poliza.DT_Polizas_OBPolizas;
import mx.com.gruposalinas.Poliza.DT_Polizas_ResponseResponse;

@SuppressWarnings("unchecked")
public interface InversionesDao {

	
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public List<LlenaComboGralDto> consultarBancosDep(int iNoEmpresa, int iInstitucion, String sIdDivisa);
	public List<ConfirmacionCargoCtaDto> consultarInstitucion();
	public List<CuentaDto> consultarContratos(int iNoEmpresa);
	public String consultarContactos(int iContacto);
	public int consultarLineaEmpresa(int iIdEmpresa);
	public List<BancoCheContratoDto> consultarBancosContrato(int iNoEmpresa, int iNoLinea, int iNoCuenta);
	public List<LlenaComboGralDto> consultarContactosInstitucion(int iInstitucion);
	public List<LlenaComboChequeraDto> consultarChequeras(int iIdBanco, int iIdEmp, int iIns, String sIdDivisa);
	public int insertarContratos(CuentaDto dtoIns);
	public int insertarCuentas(CtasContratoDto dtoIns);
	public List<OrdenInversionDto> consultarOrdenInversion(int iNoCuenta, int iNoEmpresa);
	public int eliminarContratos(int iNoCuenta, int iNoEmpresa);
	public int eliminarCuentasContrato(int iNoCuenta, int iNoEmpresa);
	public int eliminarCuentasContrato(CtasContratoDto dtoCtas);
	public int modificarContratos(CuentaDto dtoCuenta);
	public List<CuentaDto> consultarNumeroContratos(int iNoEmpresa, boolean bInternas);
	public List<CatTipoValorDto> consultarTipoValor(String sIdDivisa);
	public List<LlenaComboGralDto> consultarIdNomContactos(int iContacto1, int iContacto2, int iContacto3);
	public List<CatPapelDto> consultarPapel(String sIdTipoValor);
	public List<LlenaComboGralDto> consultarEmpresaConcentradora();
	public List<LlenaComboGralDto> consultarBancosMonitor(int iIdEmpresa, String sIdDivisa, String sBancos);
	public List<LlenaComboGralDto> consultarChequerasMonitor(int iIdEmpresa, String sIdDivisa, int iIdBanco);
	public List<CatCtaBancoDto> consultarSaldoInicialMonitor(ComunInversionesDto dto);
	public List<MovimientoDto> consultarBarridoMonitor(ComunInversionesDto dto);
	public List<MovimientoDto> consultarFondeoMonitor(ComunInversionesDto dto);
	public List<OrdenInversionDto> consultarVencimientoMonitor(ComunInversionesDto dto);
	public List<CatCtaBancoDto> consultarPorInvertirMonitor(ComunInversionesDto dto);
	public List<MovimientoDto> consultarInversionesMonitor(ComunInversionesDto dto);
	public double consultarTipoCambio(String sIdDivisa);
	public List<OrdenInversionDto> consultarOrdenInversionDos(int iNoAviso, int iNoEmpresa, String sTipoOrden, 
			int iGpoInv, Date dFecha);
	public int insertarOrdenInversion(OrdenInversionDto dto);
	public boolean consultarFechaInhabil(String sFecha);
	public int insertarParametro(ParametroDto dto);
	public Map ejecutarGenerador(GeneradorDto dto);
	public List<RetencionDto> consultarIsrRetencion(int iPlazo, String sContratoIns, int iCuenta);
	public boolean verificarManejaAnioBisiesto(int iNoInstitucion);
	public List<ConsultaOrdenInversionDto> consultarOrdenesInversion(int iIdEmpresa);
	public int actualizarAutorizaOrdenInversion(String sAutoriza, int iNoOrden);
	public List<CuentaPropiaDto> consultarCuentaPropia(int iNoCuenta, int iIdEmpresa);
	public Map<String, Object> ejecutarRevividor(RevividorDto dtoRev);
	public List<ConsultaOrdenInversionDto> consultarLiquidaOrdenInversion(Integer iIdEmpresa, Integer iAvisoInversion, Integer isUsuario);
	public List<LlenaComboGralDto> consultarBancosRegresoInversion(int iIdEmpresa, String sIdDivisa);
	public List<CuentaDto> consultarContratosLiq(int iIdEmpresa, int iNumCta);
	public List<LlenaComboChequeraDto> consultarChequerasLiq(int iIdBancoLiq, int iNoCuenta, boolean bInterna);
	public List<LlenaComboGralDto> consultarBancosCargo(int iIdEmpresa, String sIdDivisa);
	public List<CatCtaBancoDto> consultarChequerasCargo(int iIdEmpresa, int iIdBanco, String sIdDivisa);
	public int insertarParametroLiquidacion(ParametroDto dto);
	public int consultarNoCuentaEmpresa(int iNoEmpresa);
	public int actualizarEstatusOrdenInversion(int iNoOrden);
	public List<MovimientoDto> consultarVencimientoInversion(ComunInversionesDto dto);
	public List<Map<String, Object>> consultarContactoInstitucion(int iNoOrden);
	public List<LlenaComboGralDto> consultarContactoOrden(int iNoOrden);
	public List<OrdenInversionDto> consultarDiasAnual(String dFechaVen, int iBanco, String sChequera, 
														double uTasa, int iNoCuenta, int iNoDocto);
	public int actualizarTasa(int iEmpresa, double uTasa, String sNoOrden, String sFecha);
	public int consultarCveOperacionContraria(int iCveOperacion);
	public int insertarParametroVencInv(ParametroDto dto);
	public List<ComunInversionesDto> consultarBancoCheqRegreso(int iNoOrden);
	public List<ComunInversionesDto> consultarEmpresaInterna(int iIdBanco, String sIdChequera);
	public String consultarEstatusOrden(int iNoOrden);
	public int insertarOrdenReinversion(OrdenInversionDto dto);
	public int insertarParametroReinversion(ParametroDto dto);
	public List<Map<String, Object>> consultarTraspasos(int iIdTipoOperacion, String sIdTipoMovto, Map<String, Object> parametros);
	public List<Map<String, Object>> consultarOrdenInversionReport(Map parametros);
	public List<Map<String, Object>> consultarSolicitudPago(Map parametros);
	public List<Map<String, Object>> consultarInvEstHoy(ParamReportesDto dto);
	public List<Map<String, Object>> consultarVencimientoInversiones(ParamReportesDto dto);
	public List<Map<String, Object>> consultarVencInvSal2Prom(ParamReportesDto dto);
	public List<Map<String, Object>> consultarInversionesDiarias(ParamReportesDto dto);
	public List<LlenaComboEmpresasDto> llenarComboEmpresas(int idUsuario);
	public int modificarIntImpInv(List<Map<String, String>> intImpInv);
	public int modificarMoviIntIsr(List<Map<String, String>> intImpInv);
	public int validaFacultad(int facultad);
	//Grafica MontoInvertido/Fecha
	List<ConsultaOrdenInversionDto> obtenerMontoInvertidoXFecha(String sNoEmpresa, String sDivisa, String sFecIni, String sFecFin);
	public String consultarbIsr(String Isr);
	
	/*Para orden inversión*/
	public List<LlenaComboGralDto> consultarBancoOrdenInversion(Integer cveContrato, 
			Integer idEmpresa, String tipoChequera);
	public List<LlenaComboGralDto> consultarBancoOrdenIinstitucion(Integer idEmpresa, Integer cveContrato);
	public List<LlenaComboGralDto> consultarChequeraOrdenInversion(Integer idBanco, 
			Integer idEmpresa, String tipoChequera);
	public List<LlenaComboGralDto> consultarChequeraOrdenInstitucion(Integer idBanco, 
			Integer idEmpresa, Integer cuenta);
	public Integer insertarTmpInvSipo(OrdenInversionDto ordenInversionDto);
	public List<OrdenInversionDto> consultarOrdenesInvPendientes();
	public Integer insertarOrdenInversion(Integer folioSeqTpInvSipo, Integer usuario, Integer folio, String fechaHoy);
	public Integer insertarEstadoCtaInv(Integer folioSeqTpInvSipo, Integer usuario, Integer folio, String fechaHoy);
	public List<OrdenInversionDto> consultarTmpOrdenInv(Integer folioSeq);
	public Integer actualizarTmpInvSipo(Integer folioSeqTpInvSipo);
	public int actualizarEstatusOrdenInversion(int iNoOrden, String estatus);
	public List<Map<String, Object>> obtenerReporteLiquidacionInversion(Map parametros);
	public List<RubroDto> consultarRubro(String idTipoValor);
	public List<RubroDto> consultarRubroOrdenInversion(Integer idTipoValor);
	public Integer consultarFormaPagoContrato(Integer noCuenta, Integer noEmpresa);
	public List<Map<String, Object>> obtenerReporteVencimientoInversion(Map parametros);
	public Integer consultarNoClinente(Integer noCuenta);
	public int actualizarOrdenInversion(int iNoOrden, OrdenInversionDto dto);
	public Integer actualizarTmpInvSipo(OrdenInversionDto dto);
	public Integer eliminarTmpInvSipo(Integer folioSeqTpInvSipo);
	public String obtenerNoCliente(String noDocto);
	public List<Map<String, Object>> obtenerInversionesLiqVencidas(Integer usuario, Integer empresa, Integer institucion, String fechaInicial, String fechaFinal);
	public List<Map<String, Object>> obtenerInversionesVigentes(Integer usuario, Integer empresa);
	public String configuraSet(int indice);
	public Map<String, Object> insertaBitacoraPoliza(DT_Polizas_ResponseResponse[]  dt_Polizas_ResponseResponse);
	public int consultarEmpresaInversion(int noOrden);
	public List<DT_Polizas_OBPolizas> obtieneListaCancelar(int iNoOrden, String estatus, int empresa);
	public String consultarCajas();
}
