package com.webset.set.impresion.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.webset.set.impresion.dto.CartasPorEntregarDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;

public interface CartasPorEntregarDao {
	
	List<CartasPorEntregarDto> obtenerCartas(String folio, String estatus, String tipo);

	List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol);

	String actualizarEstatus(List<Map<String, String>> datos);

	List<CartasPorEntregarDto> obtenerCartasE(String folio, String idBanco, String tipo, String estatus, String fechaIni, String fechaFin);

	List<CartasPorEntregarDto> obtieneDatos(String idEmision, String idBanco);

	List<CartasPorEntregarDto> llenaFolio(String fechaIni, String fechaFin);

	List<Map<String, String>> obtenerCartasEmitidas(String folio, String idBanco, String tipo, String estatus, String fechaIni, String fechaFin);

	List<Map<String, String>> obtenerCartasDetalle(String folio, String estatus, String tipo);

	String actualizarFecha(Map<String, String> map, String fechaImp);

	
}
