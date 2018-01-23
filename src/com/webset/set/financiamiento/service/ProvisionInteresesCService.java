package com.webset.set.financiamiento.service;

import java.util.List;
import java.util.Map;

import com.webset.set.financiamiento.dto.ProvisionCreditoDTO;
import com.webset.set.utilerias.dto.LlenaComboGralDto;



public interface ProvisionInteresesCService {

	List<LlenaComboGralDto> llenarCmbEmpresa(int piNoUsuario, boolean pbMismaEmpresa, int plEmpresa);
	List<ProvisionCreditoDTO> llenarGridProvisiones(String psFecha, int plEmpresa, String psFechaIni, String psTipoFuncion, String psDivisa, int tipo);
	Map<String, Object> diaHabilReg(String fecha);
	Map<String, Object> updateProvisionEstatus(String provisiones);
	Map<String, Object> updateProvisionX(String provisiones, String pdFechaVal);

	

}
