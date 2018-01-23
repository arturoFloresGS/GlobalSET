package com.webset.set.financiamiento.service;

import java.util.List;
import java.util.Map;

import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

import net.sf.jasperreports.engine.JRDataSource;

public interface AlertaVencimientoService {

	List<LlenaComboGralDto> consultarBanco(int banco);

	List<LlenaComboGralDto> consultarLinea(int usuario);

	List<LlenaComboGralDto> consultarCredito(int banco);

	List<ControlPagosPasivos> llenarGrid(int banco, String linea, int credito, String fecha, int conso);



	String exportaExcel(String json);


	JRDataSource reportePDF2(int banco, String linea, int credito, String fecha, int conso);

	JRDataSource reportePDF2(Map map);


}
