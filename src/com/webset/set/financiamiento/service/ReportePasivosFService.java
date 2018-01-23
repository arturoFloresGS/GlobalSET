package com.webset.set.financiamiento.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.financiamiento.dto.ReportePasivosFDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ReportePasivosFService {
	
	public List<LlenaComboGralDto> obtenerEmpresas(int plUsuario, String psMenu);
	public List<ReportePasivosFDto> obtenerDivisaCreditos(int plUsuario, String psMenu);
	public List<ReportePasivosFDto> obtenerPasivosFinancieros(int noEmpresa, String json);
	public HSSFWorkbook reportePasivosFinancieros(String pasivos);
}