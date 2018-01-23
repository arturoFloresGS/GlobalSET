package com.webset.set.inversiones.middleware.service;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.inversiones.dto.BancoCheContratoDto;
import com.webset.set.inversiones.dto.ComunInversionesDto;
import com.webset.set.inversiones.dto.ConsultaOrdenInversionDto;
import com.webset.set.inversiones.dto.CtasContratoDto;
import com.webset.set.inversiones.dto.LiquidaInversionesDto;
import com.webset.set.inversiones.dto.MovimientoDto;
import com.webset.set.inversiones.dto.OrdenInversionDto;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.CatPapelDto;
import com.webset.set.utilerias.dto.CatTipoValorDto;
import com.webset.set.utilerias.dto.CuentaDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;


public interface InversionesService {
	public List<LlenaComboGralDto> llenarCombosBancosDep(int iInstitucion, String sIdDivisa, int noEmpresa);
	public List<ConfirmacionCargoCtaDto> llenarComboInstitucion();
	public List<CuentaDto> consultarContratos(int noEmpresa);
	public List<LlenaComboGralDto> obtenerNombresContactos(int iContacto1, int iContacto2, int iContacto3);
	public List<BancoCheContratoDto> llenarGridBancosContrato(int iNoCuenta, int noEmpresa);
	public List<LlenaComboGralDto> consultarContactosInstitucion(int iInstitucion);
	public List<LlenaComboChequeraDto> obtenerChequeras(int iIdBanco, int iIns, String sIdDivisa, int noEmpresa);
	@SuppressWarnings("unchecked")
	public Map insertarModificarContratos(boolean bNuevo, boolean bModifi, CuentaDto dtoCuenta, List<CtasContratoDto> listCtasCon, int noEmpresa);
	@SuppressWarnings("unchecked")
	public Map eliminarContratos(int iNoCuenta, int noEmpresa);
	public List<CuentaDto> obtenerNumeroContratos(boolean bInternas, int noEmpresa);
	public List<CatTipoValorDto> obtenerTipoValor(String sIdDivisa);
	public List<LlenaComboGralDto> llenarComboContactos(int iContacto1, int iContacto2, int iContacto3);
	public List<CatPapelDto> obtenerPapel(String sIdTipoValor);
	public List<LlenaComboGralDto> obtenerEmpresaConcentradora();
	public List<LlenaComboGralDto> obtenerBancosMonitor(String sIdDivisa, String sBancos, int noEmpresa);
	public List<LlenaComboGralDto> obtenerChequerasMonitor(int iIdEmpresa, String sIdDivisa, int iIdBanco);
	public double obtenerSaldoInicial(ComunInversionesDto dto);
	public List<MovimientoDto> obtenerBarrido(ComunInversionesDto dto);
	public List<MovimientoDto> obtenerFondeo(ComunInversionesDto dto);
	public List<OrdenInversionDto> obtenerVencimientos(ComunInversionesDto dto);
	public List<CatCtaBancoDto> obtenerPorInvertir(ComunInversionesDto dto);
	public List<MovimientoDto> obtenerInversiones(ComunInversionesDto dto);
	public boolean verificarFechaInhabil(String sFecha);
	public int cancelarOrdenesInversionSET(int iNoOrden);
	@SuppressWarnings("unchecked")
	public Map insertarOrdenInversion(OrdenInversionDto dto);
	@SuppressWarnings("unchecked")
	public Map calcularInteresImpuesto(double uCapital, double uTasaImpuesto, int iPlazo, int iDiasAnual, 
			int iCuenta, int iNoInstitucion, String sContrato, String cisr);
	public double calcularInteres(double uCapital, double uTasaImpuesto, int iPlazo, int iDiasAnual);
	public double calcularIsr(int iPlazo, boolean bAplicaBisiesto, String sContrato, int iCuenta);
	public List<ConsultaOrdenInversionDto> obtenerOrdenesInversion(int noEmpresa);
	public int actualizarAutorizaOrdenInversion(String sAutoriza, int iNoOrden);
	@SuppressWarnings("unchecked")
	public Map ejecutarRevividor(List<ConsultaOrdenInversionDto> listOrdIn, int noEmpresa);
	public List<ConsultaOrdenInversionDto> consultarLiquidaOrdenInversion(Integer iAvisoInversion, Integer noEmpresa, Integer isUsuario);
	public List<LlenaComboGralDto> obtenerBancosRegresoInversion(int iIdEmpresa, String sIdDivisa);
	public List<CuentaDto> obtenerContratosLiq(int iIdEmpresa, int iNumCta);
	public List<LlenaComboChequeraDto> obtenerChequerasLiq(int iIdBancoLiq, int iNoCuenta, boolean bInterna);
	public List<LlenaComboGralDto> obtenerBancosCargo(int iIdEmpresa, String sIdDivisa);
	public List<CatCtaBancoDto> obtenerChequerasCargo(int iIdEmpresa, int iIdBanco, String sIdDivisa);
	public Map<String, Object> liquidarInversiones(LiquidaInversionesDto dtoLiqInv, 
			   List<ConsultaOrdenInversionDto> listOrdenInv, 
			   List<BancoCheContratoDto> listBancoCargo);
	//public int cancelarOrdenesInversion(int iNoOrden);
	public List<MovimientoDto> obtenerVencimientosInversion(ComunInversionesDto dto);
	public List<LlenaComboGralDto> obtenerContactoOrden(int iNoOrden);
	public List<OrdenInversionDto> obtenerDiasAnual(String dFechaVen, int iBanco, String sChequera, 
			double uTasa, int iNoCuenta, int iNoDocto);
	public double calcularImpuesto(double uCapital, int iPlazo, int iDiasAnual, 
			int iNoContrato, int iNoCuenta, int iNoInstitucion);
	public int modificarTasa(double uTasa, String sNoOrden, String sFecha, int noEmpresa);
	public List<LlenaComboEmpresasDto> llenarComboEmpresas();
	
	@SuppressWarnings("unchecked")
	public Map ejecutarVencimientoInversion(List<MovimientoDto> listVencInv, double uImporteTxt, double uInteresTxt, 
											double uImpuestoTxt, double uTasaReinversion);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteLiquidacionInv(Map datos);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReportesInversion(Map parametros);
	public String modificarIntImpInv(List<Map<String, String>> intImpInv);
	public int validaFacultad(int facultad);
	//Grafica. MontoInvertido/Fecha.
	List<ConsultaOrdenInversionDto> obtenerMontoInvertidoXFecha(String sNoEmpresa, String sDivisa, String sFecIni, String sFecFin);
	/*PARA ORDEN INVERSION*/
	public List<LlenaComboGralDto> consultarBancoOrdenInversion(Integer cveContrato, 
			Integer idEmpresa, String tipoChequera);
	public List<LlenaComboGralDto> consultarBancoOrdenIinstitucion(Integer idEmpresa, Integer cveContrato);
	public List<LlenaComboGralDto> consultarChequeraOrdenInversion(Integer idBanco, 
			Integer idEmpresa, String tipoChequera);
	public List<LlenaComboGralDto> consultarChequeraOrdenInstitucion(Integer idBanco, 
			Integer idEmpresa, Integer cuenta);
	public Boolean insertarTmpInvSipo(OrdenInversionDto ordenInversionDto);
	public List<OrdenInversionDto> consultarOrdenesInvPendientes();
	public Map<String, Object> insertarOrdenInversion(Integer folioSeqTpInvSipo);
	
	/**
	 * Obtiene los datos para el reporte de liquidacion de inversiones. Pantalla de liquidacion
	 * @param noEmpresa
	 * @param tipoInversion
	 * @return
	 */
	public List<Map<String, Object>> obtenerRepLiquidacion(Integer noEmpresa, String tipoInversion);
	public JRMapArrayDataSource obtenerReporteLiquidacionInversion(Map parametros);	
	public JRDataSource obtenerReporteVencimientonInversion(Map parametros);	
	public Integer actualizarTmpInvSipo(OrdenInversionDto dto);
	public Integer eliminarTmpInvSipo(Integer folioSeqTpInvSipo);
	
	public boolean verificarFechaMayor(String sFecha, String sFechaReferencia);
	public List<Map<String, Object>> obtenerInversionesLiqVencidas(Integer usuario, Integer empresa, Integer institucion, String fechaInicial, String fechaFinal);
	public List<Map<String, Object>> obtenerInversionesVigentes(Integer usuario, Integer empresa);
	public Map<String, Object> cancelarOrdenesInversion(int string, String string2, String string3);
	public String consultaCajas();
}
