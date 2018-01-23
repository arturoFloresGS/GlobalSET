package com.webset.set.impresion.dao;

import java.util.List;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface AsignacionFirmasDao {
	public List<LlenaComboGralDto> llenarComboBancos();
	public List<LlenaComboGralDto> llenarComboCuentas(String idBanco);
	public List<LlenaComboGralDto> llenarComboFirmantes(String tipo,String idBanco, String cuenta );
	public String updateFirmanteDeterminado(String tipo,String idBanco, String cuenta,String idPersona);
}
