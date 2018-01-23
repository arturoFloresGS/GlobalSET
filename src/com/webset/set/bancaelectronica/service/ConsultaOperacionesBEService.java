package com.webset.set.bancaelectronica.service;

import java.util.List;

import com.webset.set.bancaelectronica.dto.ConsultaOperacionesBEDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ConsultaOperacionesBEService {
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa);
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco);
	public String exportaExcel(String datos);
	public List<ConsultaOperacionesBEDto> consultaExcel(String noEmpresa, String idBanco, String chequera, String fechaIni,String fechaFin, String tipoMov,String concepto,String detalle,String origenMovimiento);
	public List<ConsultaOperacionesBEDto> llenaGrid(String folioBanco, String idChequera);
	public List<LlenaComboGralDto> llenaComboGrupo();
	public List<LlenaComboGralDto> llenaComboRubro(String idGrupo);
	public List<ConsultaOperacionesBEDto> contabiliza(String idRubro);
}