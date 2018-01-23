package com.webset.set.financiamiento.dao;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.webset.set.financiamiento.dto.ControlPagosPasivos;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

import net.sf.jasperreports.engine.JRDataSource;

public interface AlertaVencimientoDao {

	List<LlenaComboGralDto> consultarBanco(int banco);

	List<LlenaComboGralDto> consultarLinea(int banco);

	List<LlenaComboGralDto> consultarCredito(int banco);

	List<ControlPagosPasivos> consultarDisp(int banco,String linea, int credito, String fecha, int conso);

	JRDataSource obtenerReportePDF2(int banco, String linea, int credito, String fecha, int conso);

	List<Map<String, Object>> obtenerReportePDF2(Map map);
	
	

}
