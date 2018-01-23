package com.webset.set.egresos.service;

import java.util.List;
import java.util.Map;

import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface ConfirmacionTransferenciasService {
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	public List<LlenaComboGralDto> llenaComboBanco(int noEmpresa);
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa);
	public List<MovimientoDto> consultarMovimiento(int noEmpresa, int idBanco, String idChequera, int hayBanca, int idUsuario, String idDivisa);
	public Map<String, Object> ejecutarConfirmacion(List<MovimientoDto> list, boolean bAutomatico, boolean chkAutomatico, int iTieneBanca);
}
