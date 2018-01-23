package com.webset.set.egresos.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.ParametroDto;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.OperacionDivisaDTO;
import com.webset.set.utilerias.ConsultasGenerales;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

@SuppressWarnings("unchecked")
public interface GestionDeOperacionesDivisasDAO {
	
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerDivisa(int idUsuario, int noEmpresa);
	public List<ConfirmacionCargoCtaDto> obtenerBanco(int noEmpresa, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequera(int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambio();
	public List<ConfirmacionCargoCtaDto> obtenerBancoTerceros(int noCliente, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTerceros(int noCliente, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> obtenerOperador( int noCliente );
	public List<ConfirmacionCargoCtaDto> obtenerGrupos( String idTipoMovto );
	public List<ConfirmacionCargoCtaDto> obtenerRubros( int idGrupo );
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas();
	
	public int actualizarFolioReal(String tipoFolio);
	public int seleccionarFolioReal(String tipoFolio);
	public List<ConfirmacionCargoCtaDto> obtenerCajaCuenta( int noEmpresa );
	public int InsertAceptado(int inst, ConfirmacionCargoCtaDto dto, int noFolioParam, int tipoMovto, int cuentaEmp, int noFolioDocto,
			int idCaja, String concepto, String leyenda, String obervacion, String solicita, String autoriza, int plaza,
			int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3, boolean ADU, int pedido, boolean CVDivisa, int area,
			String referencia, String sDivision, int piLote, String partida, boolean pbNomina, boolean pbProvAc);
	public int guardarCartaCVD(ConfirmacionCargoCtaDto dto);
	public Map ejecutarGenerador(int emp, int folParam,int folMovi, int folDeta, int result, String mensajeResp);
	
	public List<OperacionDivisaDTO> traerOperacionesCompraVentaDeDivisasParaPago();
	public List<OperacionDivisaDTO> traerOperacionesCompraDeTransferParaPago();
	public int updateSesionOperacionesCompraDeTransferParaPago();
	
	public int autorizaOperacionesDivisas( int noUsuario, String folios);
	public int traerFolioDetDivisas(String noDocto, int idTipoOperacion);
	public Map ejecutarPagador(StoreParamsComunDto dto);
	public int actualizaEjecucionOperacionesDivisas( int noUsuario, String folios);
	
	public List<OperacionDivisaDTO> operacionesVentaDeDivisasParaImprimir( String folios );
	public int updateCancelSesionOperacionesCompraDeTransferParaPago( String folios );
	public List<OperacionDivisaDTO> operacionesVentaDeTransferParaImprimir( String folios );
	public ParametroDto obtenerIngresoOperacionDeDivisa( String folioMov, String noDocto); 
	public int insertarParametro(ParametroDto paramDto);
	public Map<String, Object> ejecutarGenerador2(GeneradorDto dto);
	public int traerFolioDetDivisasIngreso(String noDocto, int idTipoOperacion);
	public int eliminaOperacionesDivisas( int noUsuario, Integer folios);	
	public int eliminaOperacionesTransfer( int noUsuario, Integer folios);
	public int eliminaEjecucionOperacionesDivisas( int noUsuario, String folios);
	public boolean validarUsuarioAutenticado(int idUsr,String psw);
	public Date obtenerFechaHoy();
	public String obtenerFechaHoyV2();
	

}
