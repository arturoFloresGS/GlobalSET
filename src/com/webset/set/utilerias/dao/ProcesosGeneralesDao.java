package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.GeneradorDto;
import com.webset.set.utileriasmod.dto.ProcesosGeneralesDto;

import mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_OBContabilidadElectronica;
import mx.com.gruposalinas.ContabilidadElectronica.DT_ContabilidadElectronica_ResponseContabilidadResponse;

public interface ProcesosGeneralesDao {
	public List<ProcesosGeneralesDto> llenaGrid();
	public String obtieneFecha();
	public int validaEstatus();
	public int validaEstatusSist2();
	public int actualizaEstatus(int estatus);
	public int actualizaEstatusSist2(String estatus);
	public int validaUsuariosConectados();
	public String respaldoBD(int indice);
	public Map<String, Object> correHistorico();
	public List<ProcesosGeneralesDto> procesoNeteo(String fecHoy, int noEmpresa);
	public int obtieneFolio(String tipoFolio);
	public int actualizaFolio(String tipoFolio);
	public int insertaEnParametro(ProcesosGeneralesDto objDto);
	public Map<String, Object> llamaGenerador(GeneradorDto generadorDto);
	public int creaRespaldoBD(String baseDatos, String ruta);
	public void bloqueaUsuarios();
	public List<DT_ContabilidadElectronica_OBContabilidadElectronica> obtenerDatosContabilidadElectronica();
	public String configuraSet(int usernameWsCancelacion);
	public void actualizarMovimiento(DT_ContabilidadElectronica_ResponseContabilidadResponse respuesta, DT_ContabilidadElectronica_OBContabilidadElectronica dt_ContabilidadElectronica_OBContabilidadElectronica);
	public Map<String, Object> importaProv();
	public Map<String, Object> importaPasivos();
	public int insertaValorDivisa(String[] indicadoresBanc);
	
}
