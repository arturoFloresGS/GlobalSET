package com.webset.set.flujos.service;

import java.util.List;
import java.util.Map;
import net.sf.jasperreports.engine.JRDataSource;
import com.webset.set.flujos.dto.PosicionBancariaDto;

public interface CashFlowNService {
	public PosicionBancariaDto cashFlowDatos(List<Map<String, String>> parametros);
	public String generaExcel(List<Map<String, String>> parametros);
	public List<PosicionBancariaDto> consultarDetalleFlujo(List<Map<String, String>> parametros, String idRubro);
	public JRDataSource reporteDetalleFlujo(PosicionBancariaDto dtoIn);
	public List<PosicionBancariaDto> comboGrupoRubro(String idTipoMovto);
	public List<PosicionBancariaDto> comboRubro(int idGrupo);
	public String reclasificaMovtos(int noFolioDet, int idGrupo, int idRubro,int interes,String concepto,String fecValor);
}
