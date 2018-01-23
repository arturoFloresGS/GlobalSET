package com.webset.set.bancaelectronica.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.MovimientosBEDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface MovimientosBEDao {
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public String seleccionarBancaElect(int noBanco);
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco);
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa);
	public List<MovimientosBEDto> ejecutarReporteBE (Map<String, String> datos);
	public List<MovimientosBEDto> ejecutarReporteConceptoBE(Map<String, String> datos);
}
