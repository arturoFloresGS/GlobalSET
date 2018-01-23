package com.webset.set.cashflow.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.cashflow.dto.ConceptoFlujo;
import com.webset.set.cashflow.dto.EmpresaDto;
import com.webset.set.cashflow.dto.FechaInabilDto;
import com.webset.set.cashflow.dto.FechasDto;
import com.webset.set.cashflow.dto.GrupoEmpresasDto;
import com.webset.set.cashflow.dto.ParamSpFlujoDto;
import com.webset.set.cashflow.dto.ReporteFlujoDto;
import com.webset.set.cashflow.dto.Saldo;
import com.webset.set.cashflow.dto.TipoConcepto;
import com.webset.set.cashflow.dto.TipoMovto;
import com.webset.set.cashflow.dto.TotalConcepto;
import com.webset.set.cashflow.dto.TotalDiario;
import com.webset.set.cashflow.dto.TotalIngresoEgreso;
import com.webset.set.cashflow.dto.TotalSemanaDto;
import com.webset.set.cashflow.dto.TotalTipoConcepto;

public interface CashFlowDaoInterface {
	
	public List<GrupoEmpresasDto> getGrupoEmpresasDao();
	public List<GrupoEmpresasDto> getGrupoEmpresaDao( int noGrupo );
	public List<ReporteFlujoDto> getReportesFlujoDao();
	public List<EmpresaDto> getEmpresasDao(int idGrupo, int noUsuario);
	public List <EmpresaDto> getEmpresaDao(int noEmpresa);
	public List<ReporteFlujoDto> getReporteFlujoDao( int idReporte );
	public List<FechaInabilDto> getFecInhabilDao( String sFecha );
	public List<FechasDto> getFechasSistemaDao();
	public int spFlujoDiarioRealAjustadoDao( ParamSpFlujoDto obj);
	public int spFlujoMensualAjustadoDao( ParamSpFlujoDto obj ); 
	public int spFlujoMensualComparativoOriginalAjustadoDao( ParamSpFlujoDto obj );
	public int spFlujoMensualComparativoRealAjustadoDao( ParamSpFlujoDto obj );
	public int spFlujoMensuaOriginalAjustadoRealDao( ParamSpFlujoDto obj );
	
	public List<TipoMovto> FunSQLGetIngresoEgreso();
	public List<TipoConcepto> FunSQLGetTiposConceptos( int paramNoUsuario, String paramTipoMovto );
	public List<ConceptoFlujo> FunSQLGetConceptosFlujo(int paramNoUsuario, int paramTipoConcepto,int reporte,int empresa,int grupo,String fechaini,String fechfin,String fechahoy,String idTipoMovto,String divisa);
	public int funSQLUpdateRowForTotalIngresoEgreso( int paramNoUsuario, String paramTipoMovto, int paramRow);
	public int funSQLUpdateRowForTipoConcepto(int paramNoUsuario, int lngIdTipoConcepto, int paramRow);
	public int funSQLUpdateRowForConcepto(int paramNoUsuario, int lngCveConcepto, int paramRow);
	public int funSQLUpdateRowSOA(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowSD(int noUsuario, int ntLastRow);
	public List<Saldo> funSqlGetSaldosForFlujo( int paramUsuario, String paramFecha, String paramTabla, String paramDivisa, int paramIdGrupo,int paramNoEmpresa);
	
	public List<TotalDiario> funSqlGetTotalesDiario(String paramIdDivisa, int paramUsuario);
	public List<TotalDiario> funSqlGetTotalesXWeek(String paramIdDivisa, int paramUsuario, String paramFecIni, String paramFecFin);
	public List<TotalDiario> funSqlGetTotalesXMes(String paramIdDivisa, int paramUsuario, String paramFecIni, String paramFecFin);
	public List<TotalDiario> funSqlGetTotalesXMes(String paramIdDivisa, int paramUsuario, String paramFecIni, String paramFecFin, String paramOrigen);
	public List<TotalDiario> funSqlGetTotalesXPeriodo(String paramIdDivisa, int paramUsuario);
	public List<TotalDiario> funSqlGetTotalesXPeriodo(String paramIdDivisa, int paramUsuario, int bandComparativo);
	
	public List<TotalIngresoEgreso> funSqlGetTotalesIngresoEgreso(String paramIdDivisa, int paramUsuario);	
	public List<TotalIngresoEgreso> funSqlGetTotalesXSemanaIngresoEgreso(String paramIdDivisa, int paramUsuario, String paramFecIni, String paramFecFin);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgreso(String paramIdDivisa, int paramUsuario, String paramFecIni, String paramFecFin);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgreso(String paramIdDivisa, int paramUsuario, String paramFecIni, String paramFecFin,String paramOrigen);
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgreso(String paramIdDivisa, int paramUsuario);
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgreso(String paramIdDivisa,int paramUsuario, int bandComparativo);
	
	public List<TotalTipoConcepto> funSqlGetTotalesForTypeConcepto(String paramIdDivisa, int paramUsuario);
	public List<TotalTipoConcepto> funSqlGetTotalesXSemanaForTypeConcepto(String paramIdDivisa, int paramUsuario,String paramFecIni,String paramFecFin,int week);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConcepto(String paramIdDivisa, int paramUsuario,String paramFecIni,String paramFecFin);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConcepto(String paramIdDivisa, int paramUsuario,String paramFecIni,String paramFecFin,String paramOrigen);
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConcepto(String paramIdDivisa, int paramUsuario);
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConcepto(String paramIdDivisa, int paramUsuario, int bandComparativo);
	
	public List<TotalConcepto> funSqlGetTotalesForConcepto(String paramIdDivisa, int paramUsuario);
	public List<TotalConcepto>  funSqlGetTotalesXWeekForConcepto(String paramIdDivisa,int paramUsuario,String paramFecIni,String paramFecFin);
	public List<TotalConcepto>  funSqlGetTotalesXMesForConcepto(String paramIdDivisa,int paramUsuario,String paramFecIni,String paramFecFin);
	public List<TotalConcepto>  funSqlGetTotalesXMesForConcepto(String paramIdDivisa,int paramUsuario,String paramFecIni,String paramFecFin,String paramOrigen);
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConcepto(String paramIdDivisa, int paramUsuario);
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConcepto(String paramIdDivisa, int paramUsuario, int bandComparativo);
	
	//Grafica
	List<TotalConcepto> graficarFlujoEfectivo(int noEmpresa, String sFecIni, String sFecFin);
	public List<Saldo> FunSQLSaldo_Inversion(int noUsuario, String fechaInicial, String string, String idDivisa,
			int idGrupo, int noEmpresa);
	public void sp_mensual_anual_proyectado(String idDivisa, int noUsuario, String paramFechaInicial,
			String paramaFechaFinal, int i, int j, int paramIdGrupo, int paramNoEmpresa);
	public int spFlujoMesualRealAjustadoDao(ParamSpFlujoDto obj);
	public int funSQLUpdateRowForTotalIngresoEgresoMensual(int noUsuario, String idTipoMovto, int ntLastRow);
	public int funSQLUpdateRowForTipoConceptoMesual(int noUsuario, int idTipoConcepto, int ntLastRow);
	public int funSQLUpdateRowForConceptoMensual(int noUsuario, int cveConcepto, int ntLastRow);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoMensual(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public List<TotalDiario> funSqlGetTotalesXMesMensual(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes);
	public int funSQLUpdateRowSOAMENSUAL(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowSDMENSUAL(int noUsuario, int ntLastRow);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoMensual(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoMensual(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public int funSQLUpdateRowForTotalIngresoEgresoComparativorealajustado(int noUsuario, String idTipoMovto,
			int ntLastRow);
	public int funSQLUpdateRowForTipoConceptocomparativoRealajustado(int noUsuario, int idTipoConcepto, int ntLastRow);
	public int funSQLUpdateRowForConceptoMensualcomparativorealajustado(int noUsuario, int cveConcepto,
			int ntLastRow);
	 
	public int funSQLUpdateRowForTotalIngresoEgresoAjustado(int noUsuario, String idTipoMovto, int ntLastRow);
	public int funSQLUpdateRowForTipoConceptoAjustado(int noUsuario, int idTipoConcepto, int ntLastRow);
	public int funSQLUpdateRowForConceptoAjustado(int noUsuario, int cveConcepto, int ntLastRow);
	public List<TotalDiario> funSqlGetTotalesXMesAjustado(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public int funSQLUpdateRowSOAAjustado(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowSDAjustado(int noUsuario, int ntLastRow);
	public List<TotalDiario> funSqlGetTotalesXPeriodoAjustado(String idDivisa, int noUsuario);
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoAjustado(String idDivisa, int noUsuario);
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoAjustado(String idDivisa, int noUsuario);
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoAjustado(String idDivisa, int noUsuario);
	public List<TotalDiario> funSqlGetTotalesXPeriodoSemanal(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes);
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoSemanal(String idDivisa, int noUsuario,
			String fechaInicial, String fechaFinal);
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoSemanal(String idDivisa, int noUsuario,
			String fechaInicial, String fechaFinal);
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptosemanal(String idDivisa, int noUsuario,
			String fechaInicial, String fechaFinal);
	public List<TotalDiario> funSqlGetTotalesXPeriodoMesual(String idDivisa, int noUsuario, String fechaInicial,
			String fechaFinal);
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoMensual(String idDivisa, int noUsuario,
			String fechaInicial, String fechaFinal);
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoMensual(String idDivisa, int noUsuario,
			String fechaInicial, String fechaFinal);
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoMensual(String idDivisa, int noUsuario,
			String fechaInicial, String fechaFinal);
	public int spFlujoDiarioRealDiarioDao(ParamSpFlujoDto obj);
	 
	public int spFlujoMensualOriginalDao(ParamSpFlujoDto obj);
	public int funSQLUpdateRowForTotalIngresoEgresoOriginal(int noUsuario, String idTipoMovto, int ntLastRow);
	public int funSQLUpdateRowSOAOriginal(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowSDOriginal(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowForTipoConceptoOriginal(int noUsuario, int idTipoConcepto, int ntLastRow);
	public int funSQLUpdateRowForConceptoOriginal(int noUsuario, int cveConcepto, int ntLastRow);
	public List<TotalDiario> funSqlGetTotalesXMesOriginal(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoOriginal(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoOriginal(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginal(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes);
	public List<TotalDiario> funSqlGetTotalesXPeriodoOriginal(String idDivisa, int noUsuario);
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoOriginal(String idDivisa, int noUsuario);
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoOriginal(String idDivisa, int noUsuario);
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoOriginal(String idDivisa, int noUsuario);
	public int funSQLUpdateRowForTotalIngresoEgresoOriginalAjustado(int noUsuario, String idTipoMovto, int ntLastRow);
	public int funSQLUpdateRowForTipoConceptoOriginalAjustado(int noUsuario, int idTipoConcepto, int ntLastRow);
	public int funSQLUpdateRowForConceptoOriginalAjustado(int noUsuario, int cveConcepto, int ntLastRow);
	public int funSQLUpdateRowSOAOriginalAjustado(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowSDOriginalAjustado(int noUsuario, int ntLastRow);
	public List<TotalDiario> funSqlGetTotalesXMesOriginalAjustado(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes, String tipoInfo);
	public List<TotalDiario> funSqlGetTotalesXMesOriginalAjustado2(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes, String string);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String tipoInfo);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalAjustado2(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoOriginalAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String tipoInfo);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoOriginalAjustado2(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoOriginalAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String tipoInfo);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoOriginalAjustado2(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalDiario> funSqlGetTotalesXPeriodoOriginalAjustado(String idDivisa, int noUsuario, int tipoInfo);
	public List<TotalIngresoEgreso> funSqlGetTotalesXPeriodoIngresoEgresoOriginalAjustado(String idDivisa,
			int noUsuario, int tipoInfo);
	public List<TotalTipoConcepto> funSqlGetTotalesXPeriodoForTypeConceptoOriginalAjustado(String idDivisa,
			int noUsuario, int tipoInfo);
	public List<TotalConcepto> funSqlGetTotalesXPeriodoForConceptoOriginalAjustado(String idDivisa, int noUsuario,
			int tipoInfo);
	public List<TotalDiario> funSqlGetTotalesXMesRealAjustado(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes, String string);
	public List<TotalDiario> funSqlGetTotalesXMesRealAjustado2(String idDivisa, int noUsuario, String strFecIniMes,
			String strFecFinMes, String string);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoRealAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalIngresoEgreso> funSqlGetTotalesXMesIngresoEgresoRealAjustado2(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoRealAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalTipoConcepto> funSqlGetTotalesXMESForTypeConceptoRealAjustado2(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoRealAjustado(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoRealAjustado2(String idDivisa, int noUsuario,
			String strFecIniMes, String strFecFinMes, String string);
	public int funSQLUpdateRowSOARealAjustado(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowSDRealAjustado(int noUsuario, int ntLastRow);
	 
	public List<Map<String, String>> conceptosTitulos(String movimiento); 
	public float saldoInicial(String grupo, String noempresa, String empresa, String fechaIni, String fechaFin,
			String reporte, String noGrupo, int noUsuario, String idDivisa);
	List<Map<String, String>> conceptosTitulosID(String movimiento);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalExcel(String idDivisa, int noUsuario, String mes,
			String mes2);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoAjustadoExcel(String idDivisa, int noUsuario, String mes,
			String mes2);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOriginalAjustadoExcel(String idDivisa, int noUsuario,
			String mes, String mes2);
	public List<TotalSemanaDto> funSqlGetTotalesXMesForConceptoSemanalExcel(String idDivisa, int noUsuario, String mes,
			String mes2);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoRealAjustadoExcel(String idDivisa, int noUsuario,
			String mes, String mes2);
	public int funSQLUpdateRowSOAOAR(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowSDOAR(int noUsuario, int ntLastRow);
	public int funSQLUpdateRowForTotalIngresoEgresoOAR(int noUsuario, String idTipoMovto, int ntLastRow);
	public int funSQLUpdateRowForTipoConceptoOAR(int noUsuario, int idTipoConcepto, int ntLastRow);
	public int funSQLUpdateRowForConceptoOAR(int noUsuario, int cveConcepto, int ntLastRow);
	public List<TotalConcepto> funSqlGetTotalesXMesForConceptoOARExcel(String idDivisa, int noUsuario, String mes,
			String mes2);
 
 
 
	
}