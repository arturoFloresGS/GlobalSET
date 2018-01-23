package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.EquivalenciaBancosDto;


public interface EquivalenciaBancosDao {

	List<EquivalenciaBancosDto> llenaGridBancos(String bankl, String banka, String idBanco, String descBanco);

	List<EquivalenciaBancosDto> consultarBancos(String texto);

	List<EquivalenciaBancosDto> llenarComboBanco(EquivalenciaBancosDto dto);

	String actualizaEquivaleBanco(String bankl, String idBancoGrid, String idBancoText);

	List<Map<String, String>> reporteBancosExt();

}
