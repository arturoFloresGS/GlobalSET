package com.webset.set.impresion.service;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.ParametrosCartaDto;


public interface ParametrosCartaService {
	public List<ParametrosCartaDto> llenaBanco();

	public String insertaCarta(List<Map<String, String>> registroGson);

	public List<ParametrosCartaDto> llenaGrid();

	public List<ParametrosCartaDto> verificaRegistro(int idBanco, String tipo);

	public String eliminaCarta(int idBanco, String tipo);

	public List<ParametrosCartaDto> obtieneCarta(int idBanco, String tipo);

	public String actualizaCarta(List<Map<String, String>> registroGson, String idBanco, String tipo);
	
}
