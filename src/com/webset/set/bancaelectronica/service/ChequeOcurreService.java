package com.webset.set.bancaelectronica.service;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.CriterioBusquedaDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface ChequeOcurreService {

	List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	List<LlenaComboGralDto> llenaComboBanco(int noEmpresa);
	List<MovimientoDto> consultarCheques(int noEmpresa, int idBanco);
	//Map<String, Object> ejecutar(List<MovimientoDto> movimientos, int optSantanderDirecto, int idBanco, boolean chkSantanderH2H) throws Exception;
	List<MovimientoDto> consultaCheques(CriterioBusquedaDto dtoBus);
	Map<String, Object> ejecutarCheques(List<MovimientoDto> listBus, CriterioBusquedaDto dtoBus, int h2hAfrd);

}
