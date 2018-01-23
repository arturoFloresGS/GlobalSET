package com.webset.set.utilerias.service;

import java.util.List;

import com.webset.set.utileriasmod.dto.ProcesosGeneralesDto;

public interface ProcesosGeneralesService {
	public List<ProcesosGeneralesDto> llenaGrid();
	public String obtieneFecha();
	public int validaEstatus();
	public int actualizaEstatusSist2();
	public int validaUsuariosConectados();
	public String correCierre(int noUsuario, String fecHoy);
	public String respaldoBD (int indice);
	public int generaRespaldoBD(String fecHoy);
}
