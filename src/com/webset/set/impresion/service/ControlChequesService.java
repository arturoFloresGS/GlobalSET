package com.webset.set.impresion.service;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ControlChequesService {

	List<LlenaComboEmpresasDto> llenarComboEmpresa();
	List<LlenaComboGralDto> llenarComboBanco();
	List<LlenaComboGralDto> llenarComboChequera(int idBanco, int noEmpresa);
	Map<String, Object> guardarControlCheque(ControlPapelDto controlPapel);
	List<ControlPapelDto> consultarCheques(ControlPapelDto controlPapel, boolean folioPapel, boolean estatusChequeras, boolean estatusChequerasT);
	Map<String, Object> eliminarControlCheque(ControlPapelDto controlPapel);
	Map<String, Object> modificarControlCheque(ControlPapelDto controlPapelOriginal, ControlPapelDto controlPapel);
	String exportaExcel(String datos, String titulo);

}
