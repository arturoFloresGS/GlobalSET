package com.webset.set.impresion.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.dto.ChequePorEntregarDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;

public interface ChequesPorEntregarDao {
	public List<ChequePorEntregarDto> obtenerCheques(List<Map<String, String>> datos, String entregado);
	public List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol);
	public String actualizarMovimiento(List<Map<String, String>> datos);
	//public List<LlenaComboGralDto> llenarComboBancos();
	//public List<LlenaComboGralDto> llenarComboGral(LlenaComboGralDto dto);
	public List<LlenaComboGralDto> llenarComboBeneficiario(LlenaComboGralDto dto);
	public List<LlenaComboGralDto> consultarProveedores(String texto);
	public List<ChequePorEntregarDto> llenaBanco(String idEmpresa);
	public List<ChequePorEntregarDto> llenaEmpresa();
	public List<ChequePorEntregarDto> llenaChequera(String idEmpresa, String idBanco);
}
