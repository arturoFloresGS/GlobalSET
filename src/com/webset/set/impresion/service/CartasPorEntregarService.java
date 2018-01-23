package com.webset.set.impresion.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.impresion.dto.CartasPorEntregarDto;
import com.webset.set.utileriasmod.dto.MantenimientoSolicitantesFirmantesDto;

public interface CartasPorEntregarService {

	List<CartasPorEntregarDto> obtenerCartas(String folio, String estatus, String tipo);

	List<MantenimientoSolicitantesFirmantesDto> obtenerSolicitantes(String tipoSol);


	String exportarExcel(String json);

	String cambiarEstatus(String jsonDatos);

	List<CartasPorEntregarDto> obtenerCartasE(String folio, String idBanco, String tipo, String estatus, String fechaIni, String fechaFin);

	List<CartasPorEntregarDto> obtieneDatos(String idEmision, String idBanco);

	String generaPDF(List<Map<String, String>> valorGson, List<Map<String, String>> valorBeneficiarios, String dif, String tipo, String fechaImp);

	List<CartasPorEntregarDto> llenaFolio(String fechaIni, String fechaFin);

	HSSFWorkbook reporteCartasEmitidas(String folio, String idBanco, String tipo, String estatus,String fechaIni, String fechaFin);

	
}
