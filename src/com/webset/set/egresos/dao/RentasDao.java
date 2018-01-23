package com.webset.set.egresos.dao;

/*
 * Autor: Luis Alfredo Serrato Montes de Oca
 * 22102015
 */

import java.util.Date;
import java.util.List;

import com.webset.set.egresos.dto.DatosExcelDto;

public interface RentasDao {
	public List<DatosExcelDto> validarDatosExcel(String datos);
	public int obtenerFolioCupos(int claveUsuario);
	public int insertarPropuestaPago(String claveControl, int sociedad, double total, Date fechaHoy, String concepto);
	public int actualizaMvimiento(Date fecha, String claveControl, int sociedad, 
			int numeroAcredor, String nombreAcredor, String docSap, String viaPago, 
			String banco, String cuenta, String moneda, double total, String ccid,
			String noFolioDet,String referencia, String bandera);
	public int obtenerGrupoFlujo(int sociedad);
}
