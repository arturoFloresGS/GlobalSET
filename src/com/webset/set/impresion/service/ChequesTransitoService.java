package com.webset.set.impresion.service;

import java.util.List;

import com.webset.set.impresion.dto.ChequesTransitoDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ChequesTransitoService {
	public String exportaExcel(String datos);
	public List<ChequesTransitoDto> llenaGrid(String noEmpresa, String noBanco, String idChequera, String noCheque,String fechaIni, String fechaFin, String dias);
	public String cancelarCheque(ChequesTransitoDto dto);
	public List<LlenaComboGralDto> llenarComboMotivos();
}