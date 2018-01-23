package com.webset.set.coinversion.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.coinversion.dto.ArbolEmpresaDto;
import com.webset.set.coinversion.dto.ArbolEmpresaFondeoDto;
import com.webset.set.coinversion.dto.BarridoChequerasDto;
import com.webset.set.coinversion.dto.ControlFondeoChequesDto;
import com.webset.set.coinversion.dto.DivisasEncontradasDto;
import com.webset.set.coinversion.dto.HistSaldoDto;
import com.webset.set.coinversion.dto.InteresesDto;
import com.webset.set.coinversion.dto.ParamBusquedaFondeoDto;
import com.webset.set.coinversion.dto.ParamRetornoFondeoAutDto;
import com.webset.set.coinversion.dto.ParametroDto;
import com.webset.set.coinversion.dto.SaldoChequeraDto;
import com.webset.set.coinversion.dto.SaldoDto;
import com.webset.set.coinversion.dto.TmpSdoPromDto;
import com.webset.set.coinversion.dto.TmpTraspasoFondeoDto;
import com.webset.set.coinversion.dto.VencimientoInversionDto;
import com.webset.set.seguridad.dto.EmpresaDto;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.OrdenInversionDto;
import com.webset.set.utilerias.dto.Retorno;
import com.webset.set.utilerias.dto.StoreParamsComunDto;

public interface CoinversionDao {
	
	public List<Retorno> consultarConfiguraSet();
	public List<LlenaComboGralDto> consultarDatosEmpresaRaiz(boolean bExistentes);
	public List<LlenaComboGralDto> consultarDatosBancoConcentradora(boolean bConcentradora,
			int idEmpresa, String idDivisa);
	public List<LlenaComboGralDto> consultarChequeraFondeo(int iIdEmpresa, String sIdDivisa, int iIdBanco);
	public List<ParamRetornoFondeoAutDto> consultarFondeoAutomatico(ParamBusquedaFondeoDto dto);
	public List<ParamRetornoFondeoAutDto> consultarFondeoArbol(ParamBusquedaFondeoDto dto);
	public double consultarTipoCambioHoy();
	public List<LlenaComboGralDto> consultarBancosRaiz(int iIdRaiz, String sIdDivisa);
	public String consultarIdDivisaSoin(String sIdDivisa);
	public double consultarSaldoCoinversora(int noEmpresaCon, int noEmpresaDes, String idDivisa);
	public List<CatCtaBancoDto> consultarSaldoChequera(int iIdEmpresa, String sIdChequera, int iIdBanco);
	public double consultarImporteMovimientos(int iIdEmpresa, int iNocuenta, String sIdDivisa);
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public int insertarParametro(ParametroDto dto);
	public int consultarCuenta(int idEmpresa);
	@SuppressWarnings("unchecked")
	public Map ejecutarGenerador(GeneradorDto dto);
	public List<ArbolEmpresaDto> consultarParentesco(int iIdEmpresa);
	public int insertarParAutPres(ParametroDto dto);
	public List<Map<String, Object>> consultarRevisionFondeo();
	public List<CatCtaBancoDto> consultarRevisaChequera2(int iIdEmpresa, int iIdBanco, String sIdDivisa);
	public List<Map<String, Object>> consutarTmpArbolTrasp();
	public List<ControlFondeoChequesDto> consultarRevisionPagos(int iIdBanco, String sIdChequera);
	public List<ArbolEmpresaFondeoDto> consultarTipoOperacion(int iIdEmpresaOrigen, 
			int iIdEmpresaDestino, int iTipoArbol, int orden);
	@SuppressWarnings("unchecked")
	public Map ejecutarPagador(StoreParamsComunDto dto);
	public int eliminarPagosCheques(int iNoFolioDet, int iIdBanco, String sIdChequera);
	public List<MovimientoDto> consultarPagos(ParamBusquedaFondeoDto dtoBus);
	public List<ParamRetornoFondeoAutDto> consultarDesglosePagos(ParamBusquedaFondeoDto dtoBus);
	public List<MovimientoDto> consultarFondeoCheques(String sIdChequera, int iIdBanco);
	public List<ControlFondeoChequesDto> consultarRevisionCheques(int iNoFolioDet, int iIdBanco, String sIdChequera);
	public int insertarControlFondeoCheques(int iNoFolioDet, int idBanco, String sIdChequera);
	public int eliminarControlFondeoCheques(int iNoFolioDet, int iIdBanco, String sIdChequera);
	public int insertarTmpTraspasoFondeo(TmpTraspasoFondeoDto dto);
	public void eliminarTmpTraspasoFondeo();
	public List<DivisasEncontradasDto> consultarDivisasFilialesEncontradas(int iEmpresa);
	public List<DivisasEncontradasDto> consultarDivisasFilialesNoEncontradas(int iEmpresa);
	public int actualizarEmpresa(int controladora, int nvaEmpresa);
	public int insertaSaldos(int empresa, int linea, int cuenta, int tipoSaldo);
	public int insertaFilial(int noEmpresa, String numProducto, int noPersona, int noUsuario, int noLinea, int noCuenta, Date fechaAlta,
			String idTipoCuenta, String descCuenta, String idDivisa);
	public int consultaMovimientosInversoras(int iEmpresa, int iCuenta, int iLinea);
	public List<SaldoDto> consultarSaldoFiliales(int iEmpresa, int iCuenta, int iLinea);
	public int actualizarControladora(int iEmpresa);
	public int eliminarSaldo(int iEmpresa, int iCuenta, int iLinea);
	public int eliminarCuenta(int iEmpresa, int iCuenta, int iLinea);
	public List<Map<String,Object>> consultarReporteCoinversoras(int plEmpresa, int plUsuario);
	public List<LlenaComboGralDto> consultarBancosConcentradora(boolean bConcentradora,int iIdEmpresa, String idDivisa);
	public List<LlenaComboChequeraDto> consultarChequerasBarrido(boolean bPagadora, int iBanco, int iNoEmpresa);
	public List<BarridoChequerasDto> consultarBarridoChequerasPorSaldo(int iConcentradora, String sDivisa, String sBancos);
	public int eliminarSaldosChequeras(Date dFechaHoy);
	public int consultaExistenciaArchivo(String nomArchivo);
	public List<BarridoChequerasDto> consultarChequeraConcentradora(int iBanco, String sChequera);
	public List<SaldoChequeraDto> consultarSaldoChequera(int iBanco, String sChequera, String sFecha);
	public int insertaSaldoChequera(SaldoChequeraDto dto, String sFecha);
	public int insertaArchivosSaldos(SaldoChequeraDto dto, String sFecha);
	public int actualizaEstatusSaldoChequera(String iSecuencia);
	public int insertarParametro1(ParametroDto dto);
	public List<LlenaComboEmpresasDto> consultarEmpresasCoinversionistas(int iEmpresa, int iLinea, int iUsuario);
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa, String sDivisa);
	public double consultarSaldoFinal(int iEmpresa, int iBanco, String sChequera);
	public double consultarChequesPorEntregar(int iEmpresa, int iBanco, String sDivisa, String sChequera);
	public List<Map<String,String>> consultarSaldoCreditoCoinvPorChequera(int iCoinversora, int iEmpresa, String sDivisa);
	public List<CatCtaBancoDto> consultaDivisa(int iBanco, String sChequera);
	public List<LlenaComboGralDto> consultarBancos2(int iEmpresa, String sDivisa);
	public List<LlenaComboGralDto> consultarBancosSolFondeo2(int iEmpresa, String sDivisa);
	public List<LlenaComboChequeraDto> consultarChequeras2(int iBanco, int iNoEmpresa);
	public List<Map<String,String>> consultarSaldoFinal2(int iBanco, String sChequera, int iEmpresa, int iEmpInv, String sDivisa);
	public int insertarTraspaso(ParametroDto dto);
	public double consultarInteres (int iEmpresa, String sDivisa, Date dFechaAlta, Date dFechaVenc);
	public double consultarInteres2 (int iEmpresa, String sDivisa, Date dFechaAlta, Date dFechaVenc);
	public List<Map<String,Object>> consultarIntereses(int iEmpresa, String sDivisa, Date dFechaAlta, Date dFechaVenc);
	public double consultarIsr(int iEmpresa, String sDivisa, Date dFechaIni, Date dFechaFin);
	public double consultarIsr2(int iEmpresa, String sDivisa, Date dFechaIni, Date dFechaFin);
	public List<Map<String, Object>> consultarInteresDevengado(int iEmpresa, String sDivisa, Date dFechaIni, Date dFechaFin);
	public List<InteresesDto> consultarValoresSdoPromyCapitalazacion(int iEmpresa, String sDivisa);
	public double consultarTasaIsr();
	public List<Map<String, Object>> consultarTasasInversion(int iAnio, String sMes, String sDivisa);
	public int eliminarTmpSdoProm(int iUsuario);
	public int insertarTmpSdoProm(int iUsuario, int iEmpresa, int iDias, int iLinea, Date dFecha1, Date dFecha2);
	public List<TmpSdoPromDto> consultarTmpSdoProm(int iUsuario);
	public int actualizarTmpSdoProm(InteresesDto dto, int iUsuario);
	public List<VencimientoInversionDto> consultarVencInversion(int iEmpresa, String sDivisa, Date dFecha1,Date dFecha2);
	public List<CatCtaBancoDto> consultarChequerasTraspCoinv(int iEmpresa, String sDivisa);
	public int insertaCapitalizacion(ParametroDto dto);
	public List<CatCtaBancoDto> consultarChequerasConcEmp(int iEmpresa, String sDivisa);
	public List<CatCtaBancoDto> consultarCheqCoinvMismoBanco(int iEmpresa, String sDivisa, int iBanco);
	public double consultarSaldoCredito(int iEmpresa, String sDivisa);
	public int insertarVencimientoInversion(VencimientoInversionDto dto);
	public List<Map<String,Object>> consultarReporteSaldoPromedio(int iUsuario);
	public List<OrdenInversionDto> consultarAnio();
	public List<OrdenInversionDto> consultarMes(int iAnio);
	public List<EmpresaDto> consultarEmpresasCoinv(int iUsuario, int iEmpresa, int iLinea);
	public int eliminarTmpEstadoCuenta(int iUsuarioAlta);
	public List<HistSaldoDto> consultarSaldoHistorico(int iEmpresa, int iCuenta, int iLinea, Date dFecha);
	public int insertarTmpEstadoCuenta(int iUsuario, HistSaldoDto dtoDatos);
	public List<HistSaldoDto> consultarSaldoDepRetDetalle(int iEmpresa, int iCuenta, String sDivisa, 
			Date dFechaIni, Date dFechaFin);
	public List<HistSaldoDto> consultarSaldoFin(boolean bFechaFinHoy, int iEmpresa, int iLinea, int iCuenta, 
			Date dFechaHoy, Date dFechaFin);
	public List<Map<String, Object>> consultarDatosEmpresa(int iNoPersona);
	public List<VencimientoInversionDto> consultarInteresCapitalizado(int iMes, int iAnio, 
			int iConcentradora,int iEmpresa, String sDivisa);
	public List<Map<String,Object>> consultarReporteEstadoCuenta(int iUsuarioAlta);
	public double consultarSaldoPromedio2(int iControladora,int iEmpresa, Date dFecha1, Date dFecha2, String sDivisa);
	public double consultarSaldoPromedio(int iControladora,int iEmpresa, Date dFecha1, Date dFecha2);
	public List<Map<String, Object>> consultarSaldoImporte(String sTabla, int iEmpresa, int iCuenta, 
			int iLinea, Date dFecha1, Date dFecha2);
	public List<HistSaldoDto> consultarMinMaxFecha(boolean bMinMax, int iEmpresa, int iCuenta, 
			int iLinea, Date dFecha1, Date dFecha2);
	public List<HistSaldoDto> consultarSaldoInicial(int iEmpresa, int iCuenta, int iLinea,Date dFecha);
	public List<Map<String,Object>> consultarReporteHistoricoSaldos(int iEmpresa, int iLinea, Date dFechaIni, Date dFechaFin, int iUsuario);
	public List<Map<String, Object>> reportePrueba(String parametroPrueba);
	public List<List<Object>> obtenerExcel();
	public int consultarCuentaEmpresa(int noEmpresa);	
	public List<LlenaComboGralDto> llenarTipoSaldo();
	public int buscaAbono(int tipo_saldo);
	public int buscaCargo(int tipoSaldo);	
	
}
