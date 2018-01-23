package com.webset.set.financiamiento.service;

import java.util.List;
import java.util.Map;

import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface VencimientoFinanciamientoCService {
	public List<LlenaComboGralDto> obtenerPaisVenc();
	public List<LlenaComboGralDto> obtenerEmpresas();
	public List<LlenaComboGralDto> obtenerContratos(int piEmpresa);
	public List<LlenaComboGralDto> obtenerDivisas(int noEmpresa);
	public List<AmortizacionCreditoDto> selectMovimientoAzt(String psFecIni,String psPais,
			int plEmpresa,int piBanco,String psLinea,int piTipoFinan,String psDivisa,int plCredito);
	public List<LlenaComboGralDto> obtenerBancoVenci(String psNac, String psTipoMenu);
	public List<LlenaComboGralDto> obtenerBancoPago(int piEmpresa);
	public List<AmortizacionCreditoDto> storeSelectCapital(String psLinea,int piDisposicion);
	public List<AmortizacionCreditoDto> selectPrimerAmortAct(String psLinea,int piDisposicion);
	public Map<String, Object> pagoAnticipadoParcial(AmortizacionCreditoDto dto, int noEmpresa);
	public Map<String, Object> pagoAmortizaciones(String amortizaciones, String txtDivisaPag,int txtBancoPag,String cmbChequeraPag,double txtTipoCambio);
	public Map<String, Object> pagoAnticipadoTotal(AmortizacionCreditoDto dto, int noEmpresa);
	public void enviarCorreoVencimientos();
	public Map<String, Object> pagoAmortizacionesAgrupadas(String amortizaciones,String agrupadas, int txtBancoPag,String cmbChequeraPag,double txtTipoCambio);
	public Map<String, Object> pagoAmortizacionesMezcla(String amortizaciones,String agrupadas, String separadas, String txtDivisaPag,
			int txtBancoPag, String cmbChequeraPag, double txtTipoCambio);
		
}