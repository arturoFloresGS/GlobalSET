package com.webset.set.impresion.service;

import java.util.List;

import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.dto.ChequePorEntregarDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;

public interface ChequesPorEntregarService {
	public List<ChequePorEntregarDto> obtenerCheques(String json, String entregado);
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol);
	public String enviarDatos(String jsonDatos);
	public String exportarExcel(String json);
	//public List<LlenaComboGralDto> llenarComboBancos();
	//public List<LlenaComboGralDto> llenarComboGral(LlenaComboGralDto dto);
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);
	public List<LlenaComboGralDto> consultarProveedores(String texto);
	public List<ChequePorEntregarDto> llenaBanco(String idEmpresa);
	public List<ChequePorEntregarDto> llenaEmpresa();
	public List<ChequePorEntregarDto> llenaChequera(String idEmpresa, String idBanco);
	
}
