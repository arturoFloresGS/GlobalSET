package com.webset.set.financiamiento.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.financiamiento.dto.AvalGarantiaDto;
import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ProvisionInteresesCDao {
	public List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa);
	public List<ProvisionCreditoDTO> selectProvisionHistorico(String psFecha, int plEmpresa, String psFechaIni,
			String psTipoFuncion, String psDivisa);
	public List<ProvisionCreditoDTO> selectProvisionMesHoy(String psFecha, int plEmpresa, String psFechaIni,
			 String psDivisa);
	public List<ProvisionCreditoDTO> selectGeneraProvision(String psFecha, int plEmpresa, String psDivisa);
	public List<ProvisionCreditoDTO> selectInhabil(String fecha);
	public int updateProvisionEstatus(ProvisionCreditoDTO dto);
	public int updateProvisionX(ProvisionCreditoDTO dto);
	
	
}
