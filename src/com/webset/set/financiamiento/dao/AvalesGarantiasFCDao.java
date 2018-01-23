package com.webset.set.financiamiento.dao;

import java.util.List;
import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface AvalesGarantiasFCDao {
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa);
	public List<LlenaComboGralDto> llenarCmbEmpresaAvalista(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa);
	public List<LlenaComboGralDto> llenarCmbTipoGtia();
	public List<AvalGarantiaDto> buscarAvalGtia(String psTipo, int piEmpresa, String tipoPersona);
	public int updateAvalGarantia(int empresa, int lsTipo, String clave, String descripcion, double valor,
			String fecIni, String fecFin, double pje, String vsEspecial, char tipoPersona);
	public int insertaAvalGtia(int empresa, int lsTipo, String clave, String descripcion, double valor, String idDivisa,
			String fecIni, String fecFin, double pje, String vsEspecial, char tipoPersona);
	public List<AvalGarantiaDto> existeAvalGtia(int empresa, int lsTipo,String clave, char tipoPersona);
	public List<AvalGarantiaDto> selectAvaladas(int empresa, String clave,String tipoPersona);
	public int insertaAsignacionEmp(int empresa, String clave, int empresaA, double montoAvalado,char tipoPersona);
	public int existeAvalGtiaLinea(int empresa, String clave, int empresaA,char tipoPersona);
	public int deleteAvalada(int empresa, String clave, int empresaA,char tipoPersona);
	public List<AvalGarantiaDto> reporteAvalesGtiasAvaladas(int vsTipoGtia);
	public List<AvalGarantiaDto> reporteAvalesGtiasAvalistas(int vsTipoGtia);
	
}
