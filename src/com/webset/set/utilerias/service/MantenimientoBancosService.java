package com.webset.set.utilerias.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.utileriasmod.dto.MantenimientoBancosDto;

public interface MantenimientoBancosService 
{
	public List<MantenimientoBancosDto> llenaComboBancos(int bancoNacional);
	public List<MantenimientoBancosDto> llenaGridBancos(String psBancoParam, String aba, int nacionalidad);
	public int obtieneIdBancoMax();
	public List<MantenimientoBancosDto> validaChequerasAsignadas(int idBanco);
	public int eliminaBanco(int idBanco);
	public String aceptar(List<Map<String, String>> registro);
	public HSSFWorkbook reporteBancos(String tipoBanco);
}
