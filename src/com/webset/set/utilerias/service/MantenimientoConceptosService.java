package com.webset.set.utilerias.service;
import java.util.List;
import java.util.Map;

import com.webset.set.utileriasmod.dto.MantenimientoConceptosDto;

import net.sf.jasperreports.engine.JRDataSource;

public interface MantenimientoConceptosService {
	public List<MantenimientoConceptosDto> llenaBancos();
	public List<MantenimientoConceptosDto> llenaFormaPago(String ingresoEgreso);
	public List<MantenimientoConceptosDto> llenaGrid(int idBanco, String conceptoBanco, String formaPago, String ingresoEgreso);
	public String aceptar(List<Map<String, String>> registro);
	public int eliminaConcepto(int idBanco, String conceptoBanco, String ingresoEgreso);
	public List<MantenimientoConceptosDto> llenaTipoOperacion();
	JRDataSource obtenerDatosConcepto(String nomReporte, Map<String, Object> parameters);
}
