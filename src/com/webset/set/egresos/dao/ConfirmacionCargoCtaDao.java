package com.webset.set.egresos.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.ConfirmacionCargoCtaDto;
import com.webset.set.egresos.dto.ResultadoDto;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public interface ConfirmacionCargoCtaDao {
	public List<LlenaComboGralDto> obtenerBancos(int noEmpresa);
	public List<MovimientoDto> llenarMovtosPagos(ConfirmacionCargoCtaDto dtoGrid);
	public List<ConfirmacionCargoCtaDto> llenarMovtosCargos(ConfirmacionCargoCtaDto dtoGrid);
	public Map<String, Object> ejecutarConfirmador(int folioDet, String folioBanco, int secuencia, Date fecValor, int result, String msg);
	
	//Para la pantalla de Consulta de mantenimiento de cupos
	public List<ConfirmacionCargoCtaDto> llenaComboGpoEmpresas();
	public List<ConfirmacionCargoCtaDto> llenaComboDivision();
	public List<ConfirmacionCargoCtaDto> llenaComboGpoCupo();
	public List<ConfirmacionCargoCtaDto> buscarCupos(int optCupos, int grupoEmpresa, String fecIni, String fecFin, String idDivision);
	public int actualizarFolioCupos();
	public int seleccionarFolioCupos();
	public Date obtenerFechaHoy();
	public int ejecutarAltaRegistro(List<ConfirmacionCargoCtaDto> listDatos, boolean esDivision, String sCveControls);
	public int ejecutarUpdateRegistro(List<ConfirmacionCargoCtaDto> listDatos, boolean esDivision, int i);
	public int existenMovtosSelAutoGrupo(List<ConfirmacionCargoCtaDto> listDatos, int i, String sTabla);
	public int deleteMovtosSelAutoGrupo(List<ConfirmacionCargoCtaDto> listDatos, int i);
	
	//Para la pantalla de pago de propuestas automatico
	public List<ConfirmacionCargoCtaDto> comboCveControl(int gpoEmpresa, int grupo, int idDivision);
	public List<ConfirmacionCargoCtaDto> comboClaveOperacion();
	public List<ConfirmacionCargoCtaDto> comboBancoReceptor();
	public List<ConfirmacionCargoCtaDto> comboBancoPagador(int gpoEmpresa);
	public List<ConfirmacionCargoCtaDto> comboChequeraPagadora(int gpoEmpresa, int idBcoPag);
	public List<ConfirmacionCargoCtaDto> comboCaja();
	public List<ConfirmacionCargoCtaDto> comboOrigenMovto();
	public List<ConfirmacionCargoCtaDto> comboDivision(int gpoEmpresa);
	public List<ConfirmacionCargoCtaDto> comboFormaPago();
	public List<ConfirmacionCargoCtaDto> comboDivisa();
	public List<ConfirmacionCargoCtaDto> comboRubroMovto(int gpoEmpresa);
	public List<ConfirmacionCargoCtaDto> selectAutomaticaGpo(int gpoEmpresa, int grupo, String cveControl, int idDivision, String idDivisa);
	public String consultarConfiguraSet(int indice);
	public List<ConfirmacionCargoCtaDto> selectAutorizacionProp(ConfirmacionCargoCtaDto dto);
	public List<ConfirmacionCargoCtaDto> selectPagosAutomaticos(ConfirmacionCargoCtaDto dto, List<Map<String, String>> paramsGrid);
	public List<ConfirmacionCargoCtaDto> selectEquivaleP(String noPersona);
	public Map<String, Object> ejecutarCuadrante(String idUsuario, String fechaPago, String sFolios);
	public int updateFecPropuesta(String sFolios, String fechaPago, String cveControl);
	public double tipoCambio(String idDivisa);
	public int updateCupoManualGrupo(boolean suma, double cupoAuto, double dImporteManual, int idGrupo, String fechaPago, String cveControl);
	
	//Para la pantalla de compra venta de divisas
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta(int idUsuario); 
	public List<ConfirmacionCargoCtaDto> obtenerBancoVta(int noEmpresa, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraVta ( int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo(int noEmpresa, String idDivisa, int radio, int custodia);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo (int custodia, int radio,int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> obtenerCasaCambioVta (int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerOperadorVta (int noCliente);
	public List<ConfirmacionCargoCtaDto> obtenerGrupoVta (int idUsuario);
	public List<ConfirmacionCargoCtaDto> obtenerBanco(int noCliente, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerRubroVta (int idGrupo);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraTotal (int noCliente, String idDivisa, int idBanco);
	public String fecHoy();
	public int actualizarFolioReal(String tipoFolio);
	public int seleccionarFolioReal(String tipoFolio);
	public List<ConfirmacionCargoCtaDto> obtenerCajaCuenta(int empresa);
	public int InsertAceptado(int inst, ConfirmacionCargoCtaDto dto, int noFolioParam, int tipoMovto, int cuentaEmp, int noFolioDocto,
			int idCaja, String concepto, String leyenda, String obervacion, String solicita, String autoriza, int plaza,
			int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3, boolean ADU, int pedido, boolean CVDivisa, int area, 
			String referencia, String sDivision, int piLote, String pvv_partida, boolean pbNomina, boolean pbProvAc);
	
	public int InsertAceptado2(int inst, ConfirmacionCargoCtaDto dto, int noFolioParam, int tipoMovto, int cuentaEmp, int noFolioDocto,
			int idCaja, String concepto, String leyenda, String obervacion, String solicita, String autoriza, int plaza,
			int sucursal, int grupo, int agrupa1, int agrupa2, int agrupa3, boolean ADU, int pedido, boolean CVDivisa, int area,
			String referencia, String sDivision, int piLote, String partida, boolean pbNomina, boolean pbProvAc);
			
			/*int num,  
			 String Division, int plArea, boolean pbNomina,  String partida, boolean pbProvAc,   int piLote, int noFolioParam1);
	*/
	/*ByVal pvi_NoEmpresa As Variant, ByVal pvv_noFolioParam As Variant, _
                                ByVal pvv_idFormaPago As Variant, ByVal pvv_UsuarioAlta As Variant, _
                                ByVal pvv_noCuenta As Variant, ByVal pvv_noDocto As Variant, ByVal pvv_fecValor As Variant, _
                                ByVal pvv_fecOperacion As Variant, ByVal pvv_idDivisa As Variant, _
                                ByVal pvv_fecAlta As Variant, ByVal pvv_Importe As Variant, _
                                ByVal pvv_ImporteOriginal As Variant, ByVal pvv_TipoCambio As Variant, _
                                ByVal pvv_idCaja As Variant, ByVal pvv_idDivisaOriginal As Variant, _
                                ByVal pvv_beneficiario As Variant, ByVal pvv_concepto As Variant, _
                                ByVal pvv_idBancoBenef As Variant, Optional ByVal pvv_idChequeraBenef As Variant, _
                                Optional ByVal pvv_idLeyenda As Variant, Optional ByVal pvv_idChequera As Variant, _
                                Optional ByVal pvv_Observacion As Variant, Optional ByVal pvv_noCliente As Variant, _
                                Optional ByVal pvv_solicita As Variant, Optional ByVal pvv_autoriza As Variant, _
                                Optional ByVal pvv_plaza As Variant, Optional ByVal pvv_sucursal As Variant, _
                                Optional ByVal pvv_grupo As Variant, Optional ByVal pvv_agrupa1 As Variant, _
                                Optional ByVal pvv_agrupa2 As Variant, Optional ByVal pvv_agrupa3 As Variant, _
                                Optional ByVal ADU As Boolean, Optional ByVal pvv_noPedido As Variant, _
                                Optional ByVal pvvIdRubro As String, Optional pvvCVDivisa As Boolean, _
                                Optional ByVal pvv_fecValorOriginal As String = "", _
                                Optional ByVal plArea As Long = -1, _
                                Optional ByVal psReferencia As String = ""*/
	public Map<String, Object> ejecutarGenerador(GeneradorDto generadorDto);
	
	//Para la pantalla de compra venta de transferencias
	public List<ConfirmacionCargoCtaDto> claveControl(int idUsuario);
	public List<ConfirmacionCargoCtaDto> claveControl();
	public List<ConfirmacionCargoCtaDto> DivisaLlenado (String noClaveControl);
	public List<ConfirmacionCargoCtaDto> llenaGridMovimientos(ConfirmacionCargoCtaDto dtoGrid, boolean pbCpaVtaTransfer);
	public List<ConfirmacionCargoCtaDto> obtenerDivisaEmpresa(int idUsuario); 
	public List<ConfirmacionCargoCtaDto> obtenerBancoEmpresa(String nomEmpresa, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraEmp(ConfirmacionCargoCtaDto dtoGrid);
	public List<ConfirmacionCargoCtaDto> obtenerDivisaPago(int noEmpresa);
	public List<ConfirmacionCargoCtaDto> obtenerEmpresa(int idUsuario);
	public ConfirmacionCargoCtaDto obtenerEmpresa(String idChequera);
	public List<ConfirmacionCargoCtaDto> BancoLlenado(int noEmpresa, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequera(int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> CasaCambio(int idUsuario);
	public List<ConfirmacionCargoCtaDto> BancoCasaCambio(String idDivisa, int idCasaCambio);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraCasa(int idCasaCambio, String idDivisa, int idBancoCasa);
	public List<ConfirmacionCargoCtaDto> obtenerOperador(int idCasaCambio);
	public int Update(int psFoliosCli, String idCasaCambio, String idBancoCasa, String idChequera, String idDivisa, double tipoCambio, double importe);
	public Map<String, Object> ejecutaPagador(StoreParamsComunDto dto);
	
	public List<ConfirmacionCargoCtaDto> obtenerDivisaVta (int idUsuario, int noEmpresa);
	public List<ConfirmacionCargoCtaDto> obtenerBancoAbo (int noEmpresa, String idDivisa);
	public List<ConfirmacionCargoCtaDto> obtenerChequeraAbo (int noEmpresa, String idDivisa, int idBanco);
	public List<ConfirmacionCargoCtaDto> obtenerGruposPorTipoMovto( String idTipoMovto );
	public List<ConfirmacionCargoCtaDto> llenaComboFirmas();
	public int guardarCartaCVD(ConfirmacionCargoCtaDto dto);
	public Map<String, Object> getDatosReporteCVD(String folioRep);
	public int traerNoPersona(String equivalePersona);
	
	public ResultadoDto cambioDeDivisa( String cadena );
	public int cambioDeDivisa( int noUsuario, String idDivisaPago,String idBancoPagador,String idChequeraPagadora, String tipoCambio, String folio );
	public ResultadoDto updateTransfer( String foliosHijo, String idCasaDeCambio, String idBancoCasaDeCambio, String idChequeraCasaDeCambio );
	public int guardarCartaCVT(ConfirmacionCargoCtaDto dto);
	public Map ejecutarPagador(StoreParamsComunDto dtoPagador);
	public Map<String, Object> getDatosReporteCVT(String folioRep);
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa);
	public Map<String, Object> generadorPagoParcial(String parametro, double interes, double iva);
	public List<AmortizacionCreditoDto> obtenerImportes(int folio);
	public Map<String, Object> crearInteres(int folio, double interes, int secuencia, String cveControl);
	
	
}
