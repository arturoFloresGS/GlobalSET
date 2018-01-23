package com.webset.set.impresion.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.CartasChequesDto;
import com.webset.set.impresion.dto.ParametrosCartaDto;
import com.webset.set.utilerias.dto.MapeoDto;

public interface ParametrosCartaDao {

	
	public List<ParametrosCartaDto> llenaBanco();

	

	public int insertaCarta(ParametrosCartaDto objParametrosCartaDto);



	public List<ParametrosCartaDto> llenaGrid();



	public int existeCarta(int idBanco, String tipo, ParametrosCartaDto objParametrosCartaDto);



	public List<ParametrosCartaDto> verificaRegistro(int idBanco, String tipo);



	public int eliminaCarta(int idBanco, String tipo);



	public List<ParametrosCartaDto> obtieneCarta(int idBanco, String tipo);



	public String actualizaCarta(ParametrosCartaDto objParametrosCartaDto, String idBanco, String tipo);












}
