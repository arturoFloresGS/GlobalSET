package com.webset.set.egresos.service;

/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 * 22012015
 */

import java.util.List;

import com.webset.set.egresos.dto.DatosExcelDto;

public interface RentasService {
	public List<DatosExcelDto> validarDatosExcel(String datos);
	public String crearPropuesta(String matrizDatos, double totalAprovado, double totalAprovadoDls, int sociedad);
	
	public String exportaExcel(String datos);
}
