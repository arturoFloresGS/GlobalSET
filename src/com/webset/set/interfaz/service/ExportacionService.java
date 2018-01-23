package com.webset.set.interfaz.service;

import java.util.List;
import java.util.Map;

import com.webset.set.conciliacionbancoset.dto.CriteriosBusquedaDto;
import com.webset.set.interfaz.dto.GuiaContableDto;
import com.webset.set.interfaz.dto.InterfazDto;

public interface ExportacionService {
	public List<InterfazDto> llenaComboEmpresa();
	public List<InterfazDto> llenaGrid(int noEmpresa, String fecHoy, int estatus);
	public String exportaRegistros(String folios, int noEmpresa);
	public List<GuiaContableDto> llenaComboCuentas(String noCuenta, String idTipoMovto);
	public Map<String, Object> updateMovimientoSET(
			List<GuiaContableDto> listMovs, CriteriosBusquedaDto dtoParams);
	public List<InterfazDto> llenaGridCXC(int noEmpresa, String fecHoy, int estatus);
	public String exportaRegistrosCXC(String folios);
}
