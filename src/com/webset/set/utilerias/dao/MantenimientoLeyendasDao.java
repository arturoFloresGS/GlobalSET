package com.webset.set.utilerias.dao;

import java.util.List;
import com.webset.set.utileriasmod.dto.MantenimientoLeyendasDto;


public interface MantenimientoLeyendasDao {
	public List<MantenimientoLeyendasDto> llenaGridLeyendas(String descLeyenda);
	public String insertaMantenimientoLeyendas(MantenimientoLeyendasDto dto);
	public String updateMantenimientoLeyendas(MantenimientoLeyendasDto dto);
	public String deleteMantenimientoLeyendas(MantenimientoLeyendasDto dto);
}
