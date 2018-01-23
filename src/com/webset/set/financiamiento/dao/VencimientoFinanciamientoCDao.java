package com.webset.set.financiamiento.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;

import com.webset.set.financiamiento.dto.CorreoVencimientoDto;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.BeneficiarioDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.utilerias.Bitacora;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;

public interface VencimientoFinanciamientoCDao {
	
	public List<LlenaComboGralDto> obtenerPaisVenc();
	public List<Retorno> consultarConfiguraSet();
	public List<LlenaComboGralDto> obtenerEmpresas();
	public List<LlenaComboGralDto> obtenerContratos(int piEmpresa);
	public List<LlenaComboGralDto> obtenerDivisas(int noEmpresa);
	public List<AmortizacionCreditoDto> selectMovimientoAzt(String psFecIni,String psPais,
			int plEmpresa,int piBanco,String psLinea,int piTipoFinan,String psDivisa,int plCredito);
	public List<LlenaComboGralDto> obtenerBancoVenci(String psNac, String psTipoMenu);
	public List<LlenaComboGralDto> obtenerBancoPago(int piEmpresa);
	public List<AmortizacionCreditoDto> storeSelectCapital(String psLinea,int piDisposicion);
	public List<AmortizacionCreditoDto> selectPrimerAmortAct(String psLinea,int piDisposicion);
	public void iniciaTransaccion();
	public void cancelaTransaccion();
	public void terminaTransaccion();
	public int insertPagoTotal(AmortizacionCreditoDto dto,int vlNoAmort);
	public int insertPagoInteresParcial(AmortizacionCreditoDto dto,int vlNoAmort);
	public List<AmortizacionCreditoDto> selectCapitales(AmortizacionCreditoDto dto);
	public int actPagoAnt(String psContrato ,int psDisposicion, String psFecVenc ,int piIdAmortizacion, int piNoFolioAmort,String spEstatus,double pdImporteCap ,String psFechaPago ,int sCambiaInicio ,double pdImpInteres, double pdSaldoInsoluto);
	public List<BeneficiarioDto> selectBeneficiario(String equivalente,String psDivisaPag);
	public Date obtenerFechaHoy();
	public int updateFolioCupos(int usuario);
	public int selectFolioCupos(int usuario);
	public List<ContratoCreditoDto> selectContratoCredito(String psIdContrato);
	public List<AmortizacionCreditoDto> selectDivision(int banco, String chequera);
	public int seleccionarFolioReal(String tipoFolio);
	public int inserta1(String pvvEmpresa, int plFolio, int pvvTipoDocto, String pvvFormaPago, String pvvTipoOperacion,
			int idUsuario, String pvvNoCuenta, int vlNoPersona, String pvvFecValor, String pvvFecOriginal,
			String fechaHoy, String fechaHoy2, double vdImporte, double vdImporte2, int idCaja, String vsDivisa,
			String vsDivisa2, String pvvOrigenReg, String pvvReferencia, String pvvConcepto, int pvvAplica,
			String vsEstatus, String pvvBSalvoBC, String pvvIdEstatusReg, int vBanco, String vChequera,
			String pvvFolioBanco, String pvvOrigenMov, String pvvObservacion, int pvvIdInvCBolsa, int pvvNoFolioMov,
			int pvvFolioRef, int plFolioAnt, double vdIva, int pvvLote, String pvvValor1, String pvvValor2,
			String format, String vsRubro, String vsDivision, int psNoRecibo, int psGrupo, int vsNoDocto,
			String vsProveedor, int vsAcreedor, int sBanco, String sChequera, String psFecPropuesta, String psLinea);
	public Map<String, Object> generador(GeneradorDto dto);
	public int updateMovCre(int pdFolioMov, int pdFolioDet, String clave, String fechaHoy, String contrato);
	public int updateAmortizacion1(String idContrato, String idDisposicion, String idAmortizacion, String fecVen, String pvStatus,
			String clave, String vsTipoMenu);
	public List<AmortizacionCreditoDto> selectPagosVenc(String contrato, String disposicion);
	public void actualizaDisp(String contrato, String disposicion, String estatus);
	public int insertaEncabezado(int dGrupo, String dEmpresa, String clave, String dFecha, double dTotal, String dFechaLim,int dUsuario);
	public void updateMovtosFecPropuesta(String vsFechaPropuesta, String clave,String concepto);
	public int obtenerGrupoFlujo(int empresa);
	public int updateAmortizaCapital(AmortizacionCreditoDto dto);
	public String consultarConfiguraSet(int indice);
	public List<CorreoVencimientoDto> selectDestinatarios();
	public void updateControlCorreo(String asunto, int usuario, String email);
	public List<Map<String, String>> funSQLDatosCorreo(int noCorreo);
	public List<ControlPagosPasivos> obtenerVencimientos(int empresa, int piBanco, String psLinea,String psContrato, String psAnexo);
	public List<ProvisionCreditoDTO> selectInhabil(String pvFechaInhabil);
	public String fechaHabil(String fecha);
	public List<CorreoVencimientoDto> obtenerEmpresasUsuario(int usuario);
	public int inserta1Agrupado(String pvvEmpresa, int plFolio, int pvvTipoDocto, String pvvFormaPago, String pvvTipoOperacion,
			int idUsuario, String pvvNoCuenta, int vlNoPersona, String pvvFecValor, String pvvFecOriginal,
			String fechaHoy, String fechaHoy2, double vdImporte, double vdImporte2, int idCaja, String vsDivisa,
			String vsDivisa2, String pvvOrigenReg, String pvvReferencia, String pvvConcepto, int pvvAplica,
			String vsEstatus, String pvvBSalvoBC, String pvvIdEstatusReg, int vBanco, String vChequera,
			String pvvFolioBanco, String pvvOrigenMov, String pvvObservacion, int pvvIdInvCBolsa, int pvvNoFolioMov,
			String pvvFolioRef, int plFolioAnt, String vdIva, int pvvLote, String pvvValor1, String pvvValor2,
			String format, String vsRubro, String vsDivision, String psNoRecibo, int psGrupo, int vsNoDocto,
			String vsProveedor, int vsAcreedor, int sBanco, String sChequera, String psFecPropuesta, String psLinea, int noFactura, double txtTipoCambio,String idLeyenda, int grupo,
			int invoiceType, int idGrupo, int area,String division, int sucursal, int plaza);
	public int actualizaImporteParametro(double vdImporte, int grupo);

	

}
