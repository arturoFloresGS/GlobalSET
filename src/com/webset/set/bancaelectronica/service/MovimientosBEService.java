package com.webset.set.bancaelectronica.service;

import java.util.List;

import com.webset.set.bancaelectronica.dto.MovimientosBEDto;
import com.webset.set.utilerias.dto.ComunDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface MovimientosBEService {
	public List<LlenaComboGralDto> llenarComboBancos(int noEmpresa);
	public List<LlenaComboGralDto> llenarComboChequeras(int noBanco,int noEmpresa);
	public List<ComunDto>obtenerConceptos(boolean lbGenerico, int noBanco);
	public String exportaExcel(String datos);
	public List<MovimientosBEDto> consultaExcel(String noEmpresa, String idBanco, String chequera, String fechaIni,String fechaFin, String tipoMov,String concepto,String detalle,String origenMovimiento);
}