package com.webset.set.financiamiento.dao;

import java.util.List;

import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.ReportePasivosFDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ReportePasivosFDao {
	public List<LlenaComboGralDto> obtenerEmpresas(int plUsuario,String psMenu);
	public List<ReportePasivosFDto> obtenerDivisaCreditos(int plUsuario, String psMenu);
	public List<ReportePasivosFDto> obtenerPasivosFinancieros(int noEmpresa, String json);
	public List<AvalGarantiaDto> obtenerMontoDispuestoAvalada(String idFinanciamiento, int idDisp, int noEmpresa);
	
}
