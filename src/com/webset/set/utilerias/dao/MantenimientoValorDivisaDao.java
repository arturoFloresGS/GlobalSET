package com.webset.set.utilerias.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.MantenimientoValorDivisaDto;

public interface MantenimientoValorDivisaDao {
	public List<MantenimientoValorDivisaDto> llenaComboTasas();
	public List<MantenimientoValorDivisaDto> llenaComboDivisas();
	public List<MantenimientoValorDivisaDto> llenaGridTasas(String fecha);
	public List<MantenimientoValorDivisaDto> llenaGridDivisas(String fecha);
	public List<MantenimientoValorDivisaDto> llenaGridTasasDefault();
	public List<MantenimientoValorDivisaDto> llenaGridDivisasDefault();
	public List<MantenimientoValorDivisaDto> buscaRegistroTasa(String fecha, String idTasa);
	public List<MantenimientoValorDivisaDto> buscaRegistroDivisa(String fecha, String idDivisa);
	public int actualizaTasa(List<Map<String, String>> gridTasas, int posicion, String fecha);
	public int insertaTasa(List<Map<String, String>> gridTasas, int posicion, String fecha);
	public int actualizaDivisa(List<Map<String, String>> gridDivisas, int posicion, String fecha);
	public int insertaDivisa(List<Map<String, String>> gridDivisas, int posicion, String fecha);	
	public int actualizaEstatusFecha();
} 
