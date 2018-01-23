package com.webset.set.flujos.service;

import java.util.List;
import java.util.Map;

import com.webset.set.flujos.dto.PosicionBancariaDto;

public interface PosicionBancariaService {
	public List<PosicionBancariaDto> buscaDivisas();
	public List<PosicionBancariaDto> posicionBancaria(int iPosicion);
	public List<PosicionBancariaDto> buscaDetalleBE(List<Map<String, String>> datosGrid, List<Map<String, String>> parametros);
	public PosicionBancariaDto posicionBancariaCash(List<Map<String, String>> datos, List<Map<String, String>> parametros);
}
