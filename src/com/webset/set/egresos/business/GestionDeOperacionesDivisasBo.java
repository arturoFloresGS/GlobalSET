package com.webset.set.egresos.business;

import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.webset.set.bancaelectronica.dto.ParametroDto;
import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.OperacionDivisaDTO;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;

public interface GestionDeOperacionesDivisasBo {

	
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerDivisa(int idUsuario, int noEmpresa);
	public List<ConfirmacionCargoCtaDto> obtenerBanco(int noEmpresa,String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequera(int noEmpresa,String idDivisa, int idBanco );
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambio();
	public List<ConfirmacionCargoCtaDto> obtenerBancoTerceros(int noCliente,String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTerceros (int noCliente,String idDivisa, int idBanco );
	public List<ConfirmacionCargoCtaDto> obtenerOperador(int noCliente);
	public List<ConfirmacionCargoCtaDto> obtenerGrupos( String idTipoMovto );
	public List<ConfirmacionCargoCtaDto> obtenerRubro(int idGrupo);
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas();
	public ResultadoDto crearSolicitudesCVD(ConfirmacionCargoCtaDto dto);
	
	public List<OperacionDivisaDTO> traerOperacionesCompraVentaDeDivisasParaPago();
	public List<OperacionDivisaDTO> traerOperacionesCompraDeTransferParaPago();
	
	public ResultadoDto autorizaOperacionesDivisas( String credencial,int noUsuario, String folios);
	public ResultadoDto autorizaOperacionesTransfer( String credencial,int noUsuario, String folios);
	public ResultadoDto pagarOperacionesDivisas(String bandera, int noUsuario, int noBanco, String idChequera, String folios);
	public ResultadoDto pagarOperacionesTransfer(String bandera, int noUsuario, int noBanco, String idChequera, String folios);
	public Map<String,Object> pagarOperacionesTransfer(List< Map< String, String> > registrosList);
	public Map<String,Object> pagarOperacionesTransfer( String registros );
	public ResultadoDto eliminarOperacionesDivisas(int noUsuario, String folios);
	public ResultadoDto eliminarOperacionesTransfer(int noUsuario, String folios);	
	public JRBeanCollectionDataSource  operacionesVentaDeDivisasParaImprimir(String folios);
	public JRBeanCollectionDataSource  operacionesVentaDeTransferParaImprimir(String folios);
	

	
}
