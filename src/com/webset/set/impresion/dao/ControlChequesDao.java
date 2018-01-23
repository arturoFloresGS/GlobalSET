package com.webset.set.impresion.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.impresion.dto.ControlPapelDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface ControlChequesDao {

	List<LlenaComboEmpresasDto> llenarComboEmpresa();
	List<LlenaComboGralDto> llenarComboBanco();
	List<LlenaComboGralDto> llenarComboChequera(int idBanco, int noEmpresa);
	Map<String, Object> guardarControlCheque(ControlPapelDto controlPapel, int tipoEstatus);
	List<ControlPapelDto> consultarCheques(ControlPapelDto controlPapel, boolean folioPapel, boolean estatusChequeras, boolean estatusChequerasT);
	Map<String, Object> eliminarControlCheque(ControlPapelDto controlPapel);
	Map<String, Object> modificarControlCheque(ControlPapelDto controlPapelOrig, ControlPapelDto controlPapel);
	int cambiarInactivoControlCheque(int idControlCheque);
	List<ControlPapelDto> existeChequeraControlPapel(ControlPapelDto controlPapel, int tipoEstatus);
	int existeChequeraControlPapelInactivo(ControlPapelDto controlPapel, int tipoEstatus);

}
