package com.webset.set.impresion.service;

import java.util.List;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface AsignacionFirmasService {
	public List<LlenaComboGralDto> llenarComboBancos();
	public List<LlenaComboGralDto> llenarComboCuentas(String idBanco);
	public List<LlenaComboGralDto> llenarComboFirmantes(String tipo,String idBanco, String cuenta );
	public String updateFirmanteDeterminado(String idBanco, String cuenta,String idPersonaA, String idPersonaB);
}