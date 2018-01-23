package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utileriasmod.dto.MantenimientoChequerasDto;



public interface MantenimientoChequerasDao 
{	
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	public List<MantenimientoChequerasDto> obtenerBancos(int noEmpresa);
	public List<MantenimientoChequerasDto> llenaGrid(int noEmpresa, int idBanco);
	public List<MantenimientoChequerasDto> obtieneTipoChequeras();
	public List<MantenimientoChequerasDto> llenaComboGrupo();
	public List<MantenimientoChequerasDto> obtenerBancosTodos(int bancoNacional);
	public List<MantenimientoChequerasDto> llenaComboDivision(int noEmpresa);
	public List<MantenimientoChequerasDto> llenaComboDivisa();
	public String configuraSet (int indice);
	public int eliminaChequeras(int noEmpresa, int idBanco, String idChequera, int noUsuario);
	public List<MantenimientoChequerasDto> buscaChequera (int idBanco, String idChequera);
	public int insertaChequera(MantenimientoChequerasDto objDto);
	public int actualizaChequera(MantenimientoChequerasDto objDto);
	public List<MantenimientoChequerasDto> obtieneCajas();
	public int buscaRegPendientes(int noEmpresa, int idBanco, String idChequera);
	public List<MantenimientoChequerasDto> facultadDeModificarChequera(int noUsuario, String indice);
	public int guardarChequera(MantenimientoChequerasDto mantenimientoChequerasDto);
	List<Map<String, String>> reporteChequeras(String tipoChequera, String empresa);
}
