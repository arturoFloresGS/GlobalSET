package com.webset.set.impresion.service;

import java.util.List;

import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.utilerias.dto.CajaUsuarioDto;

public interface ImpresorasService {
	public List<CajaUsuarioDto> llenaComboCajas();
	public List<MantenimientosDto> buscarImpresoras();
	public String eliminarImpre(int noImpresora);
	public String insertarImpre(int noImpresora, int noCaja, int noCharola);
}
