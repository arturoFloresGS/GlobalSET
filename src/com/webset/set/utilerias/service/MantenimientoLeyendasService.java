package com.webset.set.utilerias.service;

import java.util.List;
import com.webset.set.utileriasmod.dto.MantenimientoLeyendasDto;

public interface MantenimientoLeyendasService {
	public List<MantenimientoLeyendasDto> llenaGridLeyendas(String descLeyenda);
	public String insertaMantenimientoLeyendas(MantenimientoLeyendasDto dto);
	public String updateMantenimientoLeyendas(MantenimientoLeyendasDto dto);
	public String deleteMantenimientoLeyendas(MantenimientoLeyendasDto dto);
	public String exportaExcel(String datos);
}
