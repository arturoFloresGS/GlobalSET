package com.webset.set.financiamiento.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface AvalesGarantiasFCService {
	
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario,boolean pbMismaEmpresa, int plEmpresa) ;
	public List<LlenaComboGralDto> llenarCmbTipoGtia();
	public List<LlenaComboGralDto> llenarCmbEmpresaAvalista(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa);
	public List<AvalGarantiaDto> buscarAvalGtia(String psTipo, String piEmpresa);
	public Map<String, Object> updateAvalGarantia(String empresa, int lsTipo, String clave, String descripcion,
			double valor, String fecIni, String fecFin, double pje, String vsEspecial);
	public Map<String, Object> insertaAvalGtia(String empresa, int lsTipo, String clave, String descripcion, double valor,
			String idDivisa, String fecIni, String fecFin, double pje, String vsEspecial);
	public List<AvalGarantiaDto> selectAvaladas(String empresa, String clave);
	public Map<String, Object> insertaAsignacionEmp(String empresa, String clave, int empresaA, double montoAvalado);
	public Map<String, Object> existeAvalGtiaLinea(String empresa, String clave, int empresaA);
	public Map<String, Object> deleteAvalada(String empresa,String clave,int empresaA);
	public List<AvalGarantiaDto> reporteAvalesGtiasAvaladas(int tipo);
	public HSSFWorkbook excelAvaladas(String avaladas);
	public List<AvalGarantiaDto> reporteAvalesGtiasAvalistas(int tipo);
	public HSSFWorkbook excelAvalistas(String avaladas);
}