package com.webset.set.financiamiento.service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.financiamiento.dto.AmortizacionCreditoDto;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.BitacoraCreditoBanDto;
import com.webset.set.financiamiento.dto.ContratoCreditoDto;
import com.webset.set.financiamiento.dto.DisposicionCreditoDto;
import com.webset.set.financiamiento.dto.ObligacionCreditoDto;
import com.webset.set.financiamiento.dto.Parametro;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.Retorno;

public interface AltaFinanciamientoService {
	public List<Retorno> consultarConfiguraSet();
	public List<LlenaComboGralDto> obtenerContratos(int empresa);
	public List<LlenaComboGralDto> obtenerPais();
	public List<LlenaComboGralDto> obtenerTipoContratos(String psTipoFinan,boolean pbTipoContrato);
	public List<LlenaComboGralDto> obtenerDestinoRecursos();
	public List<LlenaComboGralDto>obtenerEmpresas(int idUsuario, boolean bMantenimiento);
	public List<LlenaComboGralDto> obtenerBancos(String psNacionalidad,String psDivisa,int noEmpresa);
	public List<LlenaComboGralDto> obtenerDivisas(boolean bRestringido);
	public List<LlenaComboGralDto> obtenerTasa();
	public List<LlenaComboGralDto> obtenerArrendadoras();
	public List<ContratoCreditoDto> obtenerContratoCredito(String clave);
	public List<ContratoCreditoDto> obtenerNoDisp(String idFin);
	public List<ContratoCreditoDto> obtenerTipoCambio(String idDiv);
	public List<LlenaComboGralDto> obtenerDisposiciones(String psIdContrato,boolean pbEstatus);
	public List<ContratoCreditoDto> selectPrefijo(int piBanco);
	public List<ContratoCreditoDto> selectConsecutivoLinea();
	public List<ContratoCreditoDto> selectInhabil(String pvFechaInhabil);
	public List<ContratoCreditoDto> selectContratoCredito(String psIdContrato);
	public Map<String, Object> altaContrato(ContratoCreditoDto dto,int noEmpresa);
	public List<ContratoCreditoDto> selectExisteDispAmort(String psIdContrato);
	public Map<String, Object> deleteDispAmortizacion(String psFinanciamiento);
	public List<DisposicionCreditoDto> selectDisposicionCred(String psIdContrato,int piDisposicion);
	public List<ContratoCreditoDto> selectAltaAmortizaciones(String psIdContrato);
	public Map<String, Object> updateLinea(ContratoCreditoDto dto);
	public List<LlenaComboGralDto> obtenerEquivalencia(String psDesBanco,int piBanco);
	public List<ContratoCreditoDto> funSQLTasa(String psTasa);
	public List<LlenaComboGralDto> funSQLComboClabe(int pvvValor2,String psDivisa,int noEmpresa);
	public List<ContratoCreditoDto> selectBancoNacionalidad(int piBanco);
	public Map<String, Object> updateLineaBancoCheq(ContratoCreditoDto dto);
	public Map<String, Object> altaDisposicion(DisposicionCreditoDto dto);
	public List<ContratoCreditoDto> selectNoDisp(String finac);
	public Map<String, Object> updateAmortizacionReestructurada(String psLinea, int piDisposicion) ;
	public List<ContratoCreditoDto> selectDivision(int piBanco, String psChequera);
	public int obtieneFolioReal(String tipoFolio);
	public Map<String, Object> altaParametro(Parametro dto,int noEmpresa) ;
	public List<DisposicionCreditoDto> selectDisp(String psIdContrato,int psIdDisp);
	public List<DisposicionCreditoDto> buscaComisiones(String psLinea,int piDisp);
	public List<AmortizacionCreditoDto> selectAmortizaciones(String psIdContrato,int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital);
	public Map<String, Object> updateDisposicion(DisposicionCreditoDto dto);
	public List<AmortizacionCreditoDto> selectExisteAmort(String psLinea , String psCredito);
	public Map<String, Object> deleteAmortizacion(String psFinanciamiento, int piDisp,boolean piInteres, boolean pbDisposicion);
	public Map<String, Object> deleteAGAsigLin(String psFinanciamiento, int piDisp);
	public Map<String, Object> deleteFactFacturas(String psFinanciamiento, int piDisp);
	public Map<String, Object> cancelaMovimiento(String psFinanciamiento, int piDisp);
	public List<LlenaComboGralDto> funSQLComboPeriodo(boolean pdAmort) ;
	public Map<String, Object> funSQLDeleteProvisiones(String psFinanciamiento, int piDisp,boolean pbEstatus);
	public List<AmortizacionCreditoDto> funSQLSelectAmortizacionesIntProv(String IdFinanciamiento , int iIdDisposicion);
	public Map<String, Object>insertBitacora(String psFinanciamiento ,int piDisposicion ,String psNota,String psFinanciamientoHijo,int noEmpresa);
	public List<BitacoraCreditoBanDto> selectBitacora(String vsContrato, int viDisp,int noEmpresa);
	public List<ObligacionCreditoDto> obtenerObligaciones(String psFinanciamiento,int noEmpresa);
	public int insertObligacion(String psFinanciamiento ,int piClave, String descripcion, int noEmpresa);
	public int deleteObligacion(List<ObligacionCreditoDto> listObligaciones, int noEmpresa);
	public List<ObligacionCreditoDto> obtenerObligacionesTotal(String psFinanciamiento, int noEmpresa);
	public List<Map<String, Object>> obtenerReporteContratos(String idFinanciamiento);
	public int obtenerDifMeses(String idFinanciamiento,String idDisp);
	public List<LlenaComboGralDto> selectComboEmpresaAval(int noEmpresa);
	public List<AvalGarantiaDto> obtenerMontoDispuestoAvalada(String idFinanciamiento,int idDisp, int noEmpresa);
	public List<LlenaComboGralDto> selectComboAvalGtia(String piEmpresaAvalista,String psDivisa, int empresa);
	public int selectAvaladaGtia(int piEmpresa,String psClave, int noEmpresa);
	public int insertAvalGtiaLin(String psFinanciamiento, int piDisposicion, double pdMontoDispuesto,String piEmpresaAvalista,
			String psClave, int noEmpresa);
	public int existeAvalGtiaLinea(List<AvalGarantiaDto> listAG, int noEmpresa) ;
	public int deleteLineaAvalada(List<AvalGarantiaDto> listAG,String linea,int credito,int noEmpresa);
	public Map<String, Object> insertAmortCapitales(String capital,char ps_tasa,double tasaVigente,
			double tasaFija, String vsBisiesto,int lFolio,String cmbPeriodo,int txtNoAmort, 
			int cmbDisp,String txtLinea,String pvTasaBase,Double txtBase,Double puntos, Double txtIva,
			int cmbDiaCorte,int txtDias, String txtComentario,String cmbDiaCorteInt,boolean insertaIntereses);
	public int selectExisteAmortizacion(String contrato,int disposicion,int noAmortizacion);
	public Map<String, Object> insertAmortInteres(String interes, String vsBisiesto, int lFolio, String ps_tasa,
			double vdTasaFij, double vdTasaVar, int txtSobreTasaCB,String cmbPeriodoInt,int txtNoAmortInt,String txtLinea, 
			int cmbDisp,boolean chkSobreTasa,String pvTasaBase,Double txtBase,Double puntos, Double txtIva,int cmbDiaCorte,
			int txtDias, String txtComentario,String cmbDiaCorteInt);
	public int existeIntProv(String psIdContrato , int psIdDisposicion ,  int iConsecutivo, Date dFecVencimiento, int noEmpresa) ;
	public int insertProvisionInteres(ProvisionCreditoDTO provision);
	public int updateProvision(ProvisionCreditoDTO provision);
	public HSSFWorkbook reporteAmortizaciones(String contrato, String disposicion,String montoAutorizado,String banco,String montoDisposicion,
			String divisa, String fechaInicio, String fechaFin, String tasa);
	public List<AmortizacionCreditoDto> selectAmortizacionesCapital(String psIdContrato, int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital);
	public List<AmortizacionCreditoDto> selectAmortizacionesInteres(String psIdContrato, int piDisposicion,boolean pbCambioTasa ,String psTipoMenu ,String psProyecto ,int piCapital,int dias);
	public void deleteProvisionesFuturas(String IdFinanciamiento,int iIdDisposicion, Date vsFechaFin);
	public Map<String, Object> provision(String contrato, int disposicion, int empresa);
}