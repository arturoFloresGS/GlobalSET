package com.webset.set.bancaelectronica.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.ConsultaOperacionesBEDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ConsultaOperacionesBEDao {
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public String seleccionarBancaElect(int noBanco);
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco);
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa);
	public List<ConsultaOperacionesBEDto> ejecutarReporteBE (Map<String, String> datos);
	public List<ConsultaOperacionesBEDto> ejecutarReporteConceptoBE(Map<String, String> datos);
	public List<ConsultaOperacionesBEDto> llenaGrid(String folioBanco, String idChequera);
	public List<LlenaComboGralDto> llenaComboGrupo();
	public List<LlenaComboGralDto> llenaComboRubro(String idGrupo);
	public List<ConsultaOperacionesBEDto> contabiliza(String idRubro);
}
