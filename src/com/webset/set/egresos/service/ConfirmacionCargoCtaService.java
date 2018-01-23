package com.webset.set.egresos.service;

import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface ConfirmacionCargoCtaService {
	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa);
	public List<MovimientoDto> llenarMovtosPagos(ConfirmacionCargoCtaDto dtoGrid);
	public List<ConfirmacionCargoCtaDto> llenarMovtosCargos(ConfirmacionCargoCtaDto dtoGrid);
	public Map<String, Object> ejecutarConfirmacionCargoCta(List<MovimientoDto> listPagos, List<ConfirmacionCargoCtaDto> listCargos, double diferencia, double permisible);
	
	//Para la pantalla de Consulta de mantenimiento de cupos
	public List<ConfirmacionCargoCtaDto> llenaComboGpoEmpresas();
	public List<ConfirmacionCargoCtaDto> llenaComboDivision();
	public List<ConfirmacionCargoCtaDto> llenaComboGpoCupo();
	public List<ConfirmacionCargoCtaDto> buscarCupos(int optCupos, int grupoEmpresa, String fecIni, String fecFin, String idDivision);
	public Map<String, Object> ejecutarAltaRegistro(List<ConfirmacionCargoCtaDto> listDatos, boolean insertar, boolean esDivision);
	public Map<String, Object> eliminarRegistro(List<ConfirmacionCargoCtaDto> datos);
	
	//Para la pantalla de pago de propuestas automatico
	public List<ConfirmacionCargoCtaDto> llenaComboCveControl(int gpoEmpresa, int grupo, int idDivision);
	public List<ConfirmacionCargoCtaDto> llenaComboValor(int tipoCombo, int gpoEmpresa, int idBcoPag);
	public List<ConfirmacionCargoCtaDto> selectCupos(int gpoEmpresa, String idDivisa, int grupo, String cveControl, boolean bCambioMMF, int idDivision);
	public List<ConfirmacionCargoCtaDto> selectPagosAutomaticos(ConfirmacionCargoCtaDto dto, List<Map<String, String>> paramsGrid);
	public Map<String, Object> ejecutarDatos(List<Map<String, String>> parametros, String fechaPago, String cveControl, double cupoAuto, double totPropuesto, String idDivisa, int idGrupo);
	public String validaAgregarQuitar(ConfirmacionCargoCtaDto dto);
	
	//Para la pantalla de compra venta de divisas
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta(int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerBancoVta(int noEmpresa, String idDivisa);
	
	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo(int noEmpresa, String idDivisa, int radio, int custodia);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo(int custodia, int radio, int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambioVta(int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerOperadorVta(int noCliente);
	public List<ConfirmacionCargoCtaDto> obtenerGrupoVta(int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerBanco(int noCliente, String idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerRubroVta(int idGrupo);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTotal(int noCliente, String idDivisa, int idBanco);
	public String fecHoy();
	public String ejecutar(ConfirmacionCargoCtaDto dto);
	
	//Para la pantalla de compra venta de transferencias
	public List<ConfirmacionCargoCtaDto> claveControl(int idUsuario);
	public List<ConfirmacionCargoCtaDto> DivisaLlenado(String noClaveControl);
	public List<ConfirmacionCargoCtaDto> llenaGridMovimientos(ConfirmacionCargoCtaDto dtoGrid);
	public List<ConfirmacionCargoCtaDto> obtenerDivisaEmpresa(int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerBancoEmpresa(String nomEmpresa, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraEmp(ConfirmacionCargoCtaDto dtoGrid);
	public List<ConfirmacionCargoCtaDto> obtenerDivisaPago(int noEmpresa);
	public List<ConfirmacionCargoCtaDto> obtenerEmpresa(int idUsuario);
	public List<ConfirmacionCargoCtaDto> BancoLlenado(int noEmpresa, String idDivisa); 
	public List<ConfirmacionCargoCtaDto> obtenerChequera(int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> CasaCambio(int idUsuario);
	public List<ConfirmacionCargoCtaDto> BancoCasaCambio(String idDivisa, int idCasaCambio);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraCasa(int idCasaCambio, String idDivisa, int idBancoCasa);
	public List<ConfirmacionCargoCtaDto> obtenerOperador(int idCasaCambio);
	public int Update(int psFoliosCli, String idCasaCambio, String idBancoCasa, String idChequera, String idDivisa, double tipoCambio, double importe );
	public Map<String, Object> ejecutaPagador(List<ConfirmacionCargoCtaDto> listGridFon, String sBandera, String usuario, String pi_Banco, String chequera, 
			   int pri, String ter, int seg, String ps_folios, String pi_folios_rech, String ps_folios_rech, int cua);
	
	public List<ConfirmacionCargoCtaDto> obtenerChequeraVta(int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta(int idUsuario, int noEmpresa);
	
	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo(int noEmpresa, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo(int noEmpresa, String idDivisa, int idBanco);
	
	public List<ConfirmacionCargoCtaDto> obtenerGruposPorTipoMovto( String idTipoMovto );
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas();
	public ResultadoDto ejecutar2(ConfirmacionCargoCtaDto dto);
	public Map<String, Object>  reporteContratoCompraVentaDeDivisas(String folioRepCVD);
	public int traerNoPersona(String equivalePersona);
	
	public ResultadoDto ejecutarCompraDeTransfer( String folioPadre 		  , String foliosHijo 		 	 , String numRegistros 		 	, 	    
			  String noProveedor 		  , String nomProveedor 		 , String idDivisaTransfer     	,
			  String idBancoProveedor     , String descBancoProveedor    , String idChequeraProveedor   ,
			  String descDivisaTransfer   , String totalPagoTrans 	 	 , String idCasaDeCambio 		,
			  String nomCasaDeCambio      , String idBancoCasaDeCambio   , String nomBancoCasaDeCambio  , 				
			  String chequeraCadaDeCambio , String tipoDeCambio          , String idBancoPagador 		,
			  String nomBancoPagador      , String idChequeraPagadora    , String idDivisaDePago        ,
			  String descDivisaPago       , int noUsuario );

	public Map<String, Object>  reporteContratoCompraTransfer(String folioRepCVD);
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa);
	public Map<String, Object> pagoParcial(double importeTotal, int folio, int piFolioRech, int psFolioRech,
			double saldoPendiente, String origenMov, double interes, double iva);
	public List<AmortizacionCreditoDto> obtenerImportes(int folio);
	Map<String, Object> crearInteres(int folio, double interes, int secuencia, String cveControl);

}