package com.webset.set.flujos.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.flujos.dto.PosicionBancariaDto;

public interface CashFlowNDao {
	public List<PosicionBancariaDto> cashFlowDatos(List<Map<String, String>> parametros, String tipoMovto);
	public String configuraSET(int indice);
	public List<PosicionBancariaDto> consultarDetalleFlujo(List<Map<String, String>> parametros, String idRubro);
	public List<PosicionBancariaDto> cashFlowDatosDiarios(List<Map<String, String>> parametros, String tipoMovto);
	public List<Map<String, Object>> reporteDetalleFlujo(PosicionBancariaDto dtoIn);
	public List<PosicionBancariaDto> comboGrupoRubro(String idTipoMovto);
	public List<PosicionBancariaDto> comboRubro(int idGrupo);
	public int reclasificaMovtos(int noFolioDet, int idGrupo, int idRubro);
	public int diaSemana(String dia);
	public List<PosicionBancariaDto> totalSdoChequera(List<Map<String, String>> parametros, Date fenIni, int tipoRep);
	public List<PosicionBancariaDto> totalIngresosEgresos(List<Map<String, String>> parametros, String tipoMovto, int tipoRep);
	public int insertaInteres(int noFolioDet,int interes,String concepto,int idGrupo,int idRubro,String fecValor);
}
