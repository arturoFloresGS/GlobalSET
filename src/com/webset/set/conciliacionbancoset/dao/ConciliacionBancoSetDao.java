package com.webset.set.conciliacionbancoset.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.conciliacionbancoset.dto.ConciliaBancoDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.CriteriosReporteDto;
import com.webset.set.conciliacionbancoset.dto.CruceConciliaDto;
import com.webset.set.conciliacionbancoset.dto.ParamBusquedaDto;
import com.webset.set.conciliacionbancoset.dto.MonitorConciliaGenDTO;
import com.webset.set.conciliacionbancoset.dto.TmpBancoSetMonitorDto;
import com.webset.set.conciliacionbancoset.dto.TmpBcoSetResumenDto;
import com.webset.set.conciliacionbancoset.dto.TmpConcenConciliaDto;
import com.webset.set.ingresos.dto.CuentaContableDto;
import com.webset.set.utilerias.dto.CatCtaBancoDto;
import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;
import com.webset.set.utilerias.dto.ParametroDto;

public interface ConciliacionBancoSetDao {
	
	public List<LlenaComboGralDto> consultarBancos(int iEmpresa);
	public List<CatCtaBancoDto> consultarFechaConciliacion(int iBanco, int tEmpresa, int iEmpresa, int iUsuario);
	public List<CatCtaBancoDto> consultarChequerasConciliar(int noBanco, int noEmpresa, int noUsuario);
	@SuppressWarnings("unchecked")
	public Map ejecutaSPconciliaBancoSet(ParamBusquedaDto dto);
	@SuppressWarnings("unchecked")
	public String ejecutaSPconciliaBancoMovto(ParamBusquedaDto dto);
	public List<LlenaComboChequeraDto> consultarChequeras(int iBanco, int iEmpresa, int iOpc);
	public List<LlenaComboChequeraDto> consultarEstatus(boolean bCancelacion);
	public int borrarTmpControlConcilia(int iUsuario);
	public List<Map<String, Object>> consultarTmpControlConcilia(int iBanco, String sIdChequera, boolean bCancelacion);
	public int insertarTmpControlConcilia(int idBanco, String sIdChequera, int iUsuario, boolean bCancelacion);
	public List<ConciliaBancoDto> consultarMovsBanco(CriteriosBusquedaDto dto);
	public List<MovimientoDto> consultarMovsEmpresa(CriteriosBusquedaDto dto);
	public List<Map<String, Object>> consultarContabiliza(String sRubro);
	public List<CatCtaBancoDto> consultarEmpDiv(CriteriosBusquedaDto dto);
	public List<Map<String, Object>> consultarIngresoEgreso(CriteriosBusquedaDto dto);
	public int seleccionarFolioReal(String tipoFolio);
	public int actualizarFolioReal(String tipoFolio);
	public int insertarParametro(ParametroDto paramDto);
	@SuppressWarnings("unchecked")
	public Map ejecutarGenerador(GeneradorDto dto);
	public int actualizarConciliaBanco(int iEmpresa, int iBanco, String sChequera, int iSecuencia);
	public int actualizarMovtoIdentificado(int iEmpresa, int iBanco, String sChequera, int iSecuencia);
	public int actualizarCbMovimiento(int iFolioDet, String sGenConta, double uImporte, Date dFecha, String sNomComputadora,
			int iBanco, String sChequera, int iUsuario, String sHora, String sReferencia);
	public int insertarCruceConcilia(CruceConciliaDto dto);
	public List<CatCtaBancoDto> obtenerCuenta(int iBanco, int iEmpresa, String sChequera);
	public int insertarParametro1(ParametroDto paramDto);
	public int actualizarHistMovimiento(String sEstatus, int iGrupo, Date dFecha);
	public int actualizarConciliaBanco(String sEstatus, String sAclaracion, int iEmpresa, int iBanco, String sChequera, int iGrupo);
	public int actualizarEstatusMI(int iEmpresa, int iBanco, String sChequera, int iGrupo);
	public List<ConciliaBancoDto> consultarCancMovsBanco(CriteriosBusquedaDto dto);
	public List<CruceConciliaDto> consultarExportado(int iGrupo);
	public List<MovimientoDto> consultarCancMovsEmpresa(CriteriosBusquedaDto dto);
	public int actualizarDesconciliaHistorico(int iFolioDet);
	public int insertarDetalleDesconciliacion(int iFolioDet, int iSecuencia, int iUsuario, String sFecCancela, String sObservaciones);
	public int actualizarDesconciliacion(int iFolios);
	public int actualizarHistMovimiento(int iGrupo);
	public int actualizarConciliaBanco(int iGrupo);
	public int borrarCruceConcilia(int iGrupo);
	public List<Map<String,Object>> consultarReporteCancelados(int iEmpresa);
	public List<MovimientoDto> consultarMovsFicticios(CriteriosBusquedaDto dto);
	public int actualizarFicticios(int iFolio, int iEmpresa);
	public int insertarDetalleFicticios(int iFolio, String sTipo, int iUsuario, Date dFecha, String sObservacion);
	public List<Map<String,Object>> consultarReporteMovsFicticios(int iEmpresa);
	public List<ConciliaBancoDto> consultarMovsDuplicados(CriteriosBusquedaDto dto);
	public int actualizarDuplicados(int iSecuencia, int iEmpresa);
	public int insertarDetalleDuplicadosBanco(int iSecuencia, int iUsuario, Date dFecha, String sObservacion);
	public List<Map<String,Object>> consultarReporteMovsDuplicados(int iEmpresa);
	public List<LlenaComboGralDto> consultarBancosEmpresas(String sEmpresas);
	public List<CatCtaBancoDto> consultarChequerasBanco(String sEmpresas, int iBanco);
	public List<Map<String, Object>> consultarReporteconAclaracion(CriteriosReporteDto dto);
	public List<Map<String, Object>> consultarReporteConciliadoAutomatico(CriteriosReporteDto dto);
	public List<Map<String, Object>> consultarReporteMovimientosDetectados(CriteriosReporteDto dto);
	public List<Map<String, Object>> consultarReporteAjustes(CriteriosReporteDto dto);
	public List<Map<String, Object>> consultarReporteMovimientosFicticios(CriteriosReporteDto dto);
	public int borraTmpConciliaSetBanco(int iUsuario);
	public List<Map<String, Object>> consultarFechaSaldo(int iEmpresa, int iBanco, String sChequera);
	public int borraTmpConcenConcilia(int iIdUsuario);
	public List<TmpConcenConciliaDto> consultarTmpConcenConcilia(ParamBusquedaDto dto);
	public int insertaTmpConcenConcilia(TmpConcenConciliaDto dto);
	public List<Map<String, Object>> consultarReportePendientes(int iIdUsuario);
	public int borraTmpResumen(int iIdUsuario);
	public List<TmpBcoSetResumenDto> consultarTmpBancoSetResumen(ParamBusquedaDto dto);
	public int insertaTmpBcoSetResumen(TmpBcoSetResumenDto dto);
	public List<Map<String, Object>> consultarReporteResumenPendientes(int iIdUsuario);
	public int borraTmpMonitor(int iIdUsuario);
	public List<TmpBancoSetMonitorDto> consultarTmpBcoSetMonitor(ParamBusquedaDto dto);
	public int insertarTmpBcoSetMonitor(TmpBancoSetMonitorDto dto);
	public List<Map<String, Object>> consultarReporteMonitor(int iIdUsuario);
	public List<MovimientoDto> consultarMovsEmpresaVIngresos(
			CriteriosBusquedaDto dto);
	public int updateMovimientoSETVIngresos(
			String string, CriteriosBusquedaDto dto);
	public List<CuentaContableDto> getCuentaContable(int noEmpresa, String idGrupo, String idRubro);
	
	public int insertaDatosConta(CriteriosBusquedaDto listMovtos, int i, int noFolioDet);
	public int existeFlujoMovtos(int noFolioDet);
	public int updateDatosConta(CriteriosBusquedaDto listMovtos, int i, int noFolioDet);
	public int existeMov(int noFolioDet);
	public int pasaHistAMov(int noFolioDet);
	
	public List<CuentaContableDto> consultarFacturasCXC(String noCliente, int noEmpresa);
	public int clasificaIngresos(List<Map<String, String>> datosGrid, int i, boolean ietu, boolean iva);
	public List<LlenaComboGralDto> llenarComboDeptos(int noEmpresa);
	public List<LlenaComboGralDto> llenarComboEmpresa(int iUsuario);
	public Integer obtenerNoFolioDet(Integer noFolioParam);
	public Map<String, String> consultarHeadersMonitor(CriteriosBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerMovsSETMonitor(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerMovsBANCOMonitor(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerMovsConcAutomYManualSET(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerMovsConcAutomYManualBANCO(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerAclaradosYDetectadosSET(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerAclaradosYDetectadosBANCO(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerPorConciliarSET(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerPorConciliarBANCO(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerPendientesSET(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerAjusteSET(ParamBusquedaDto dto);
	public MonitorConciliaGenDTO obtenerSaldosInicialesMonitor(ParamBusquedaDto dto);
	
}
