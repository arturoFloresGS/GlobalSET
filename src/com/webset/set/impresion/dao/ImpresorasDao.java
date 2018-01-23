package com.webset.set.impresion.dao;

import java.util.List;

import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.utilerias.dto.CajaUsuarioDto;

public interface ImpresorasDao {
	public List<CajaUsuarioDto> llenaComboCajas();
	public List<MantenimientosDto> buscarImpresoras();
	public int eliminarImpre(int noImpresora);
	public int eliminarImpreCharola(int noImpresora);
	public int buscarImpreEsp(int noImpresora);
	public int insertarImpre(int noImpresora, int noCaja, int noCharola);
}
