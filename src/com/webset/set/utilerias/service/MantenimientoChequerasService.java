package com.webset.set.utilerias.service;

import java.util.List;

import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utileriasmod.dto.MantenimientoChequerasDto;

import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface MantenimientoChequerasService 
{	
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	public List<MantenimientoChequerasDto> obtenerBancos(int noEmpresa);	
	public List<MantenimientoChequerasDto> llenaGrid(int noEmpresa, int idBanco);
	public List<MantenimientoChequerasDto> obtieneTipoChequeras();
	public List<MantenimientoChequerasDto> llenaComboGrupo();
	public List<MantenimientoChequerasDto> obtenerBancosTodos(int bancoNacional);
	public List<MantenimientoChequerasDto> llenaComboDivision(int noEmpresa);
	public List<MantenimientoChequerasDto> llenaComboDivisa();
	public String configuraSet(int indice);
	public int eliminaChequeras(int noEmpresa, int idBanco, String idChequera, int noUsuario);
	public String aceptar(List<Map<String, String>> registro);
	public List<MantenimientoChequerasDto> obtieneCajas ();
	public String facultadDeModificarChequera(int noUsuario, String indice);
	public Map<String, Object> guardarChequera(String jString);
	public HSSFWorkbook reporteChequeras(String tipoChequera, String empresa);
}
