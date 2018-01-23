package com.webset.set.prestamosinterempresas.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import com.webset.set.prestamosinterempresas.dto.ParamComunDto;
import com.webset.set.prestamosinterempresas.dto.ParamInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.RetInteresPresNoDoc;
import com.webset.set.prestamosinterempresas.dto.SolTraspasoCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.ParametroDto;

public interface PrestamosInterempresasService {

	public List<LlenaComboGralDto> obtenerEmpresasRaiz(boolean bExistentes);
	public List<LlenaComboGralDto> obtenerEmpresasHijo(int iEmpresaRaiz);
	public String obtenerArbolEmpresa(int iEmpresaRaiz);
	public List<LlenaComboGralDto> obtenerEmpresaConcentradora();
	public List<LlenaComboGralDto> obtenerBancosEmpresa(int iIdEmpresa, int iIdBanco ,String sIdCheqD);
	public List<LlenaComboGralDto> obtenerEmpresasDestino(int iIdEmpresa);
	public List<LlenaComboGralDto> obtenerChequeras(int iIdEmpresa, int iIdBanco, String sIdCheqD, int iIdBancoA);
	public double obtenerSaldoFinal(int iIdEmpresa, int iIdBanco, String sIdChequera);
	public Map<String, Object> obtenerSaldoFinalYCoinversion(int iEmpresaOrigen, int iEmpresaDes,
			int iIdBancoA, String sIdChequeraA);
	public Map<String, Object> realizarSolTraspCred(SolTraspasoCreditoDto dto);
	public List<LlenaComboGralDto> obtenerEmpresasArbol();
	public Map<String, Object> realizarPagoTraspCred(SolTraspasoCreditoDto dto);
	public List<RetInteresPresNoDoc> obtenerConsultaInteresPresNoDoc(ParamInteresPresNoDoc dto);
	public ArrayList<String> ejecutarCalculoInteres(List<ParametroDto> listParam, ParamComunDto dtoComun);
	public List<LlenaComboGralDto> obtenerPeriodosPrestamos();
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReportePresNoDoc(Map parametros);
	public List<LlenaComboGralDto> obtenerAnios();
	public List<LlenaComboGralDto> obtenerMes(int iAnio);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerRepInteresNeto(Map params);
	public List<LlenaComboGralDto> obtenerSectores();
	public List<LlenaComboGralDto> obtenerEmpresasArbolUsuario();
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteEstadoDeCuentDeCredito(Map params);
	@SuppressWarnings("unchecked")
	public JRDataSource obtenerReporteSolicitudesDeCredito(Map params);
	public List<Map<String, Object>> obtenerFlujoNeto(ParamComunDto dto);
	public JRDataSource obtenerReporteFlujoNeto(ParamComunDto dto);
	public JRDataSource obtenerReporteArbolEmpresa();
	public String agregarNodosArbol(boolean bNvoHijo, String sRuta, 
			int iIdEmpresaRaiz, int iIdEmpresa, double uMonto);
	public String eliminarNodosArbol(int iIdEmpresaRaiz, int iIdEmpresaActual, int iIdEmpresaPadre);
	
}
