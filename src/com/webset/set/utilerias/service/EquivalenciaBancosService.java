package com.webset.set.utilerias.service;

import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.utilerias.dto.EquivalenciaBancosDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;


public interface EquivalenciaBancosService {

	List<EquivalenciaBancosDto> llenaGridBancos(String bankl, String banka, String idBanco, String descBanco);

	List<EquivalenciaBancosDto> consultarBancos(String texto);

	List<EquivalenciaBancosDto> llenarComboBanco(EquivalenciaBancosDto dto);

	String actualizaEquivaleBanco(String bankl, String idBancoGrid, String idBancoText);

	HSSFWorkbook reporteBancosExt();

}
