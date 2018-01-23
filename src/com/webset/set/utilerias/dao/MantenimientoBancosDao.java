package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.MantenimientoBancosDto;
public interface MantenimientoBancosDao {
	
	public List<MantenimientoBancosDto> llenaComboBancos(int bancoNacional);
	public String buscaAba(String aba);
	public List<MantenimientoBancosDto> llenaGridBancos(String descBanco, String aba, int nacionalidad);
	public int obtieneIdBancoMax();
	public List<MantenimientoBancosDto> validaChequerasAsignadas(int idBanco);
	public int eliminaBanco(int idBanco);
	public List<MantenimientoBancosDto> buscaBancoExistente(int idBanco);
	public int actualizaBanco(MantenimientoBancosDto objDto);
	public int insertaBanco(MantenimientoBancosDto objDto);
	public List<Map<String, String>> reporteBancos(String tipoBanco);
}
