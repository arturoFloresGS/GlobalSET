package com.webset.set.impresion.dao;

import java.util.List;

import com.webset.set.impresion.dto.MantenimientosDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;

public interface MantenimientosDao {
	//Patanlla de Mantenimiento de Firmantes
	public List<LlenaComboGralDto> llenarComboNoFirmantes( );
	public List<MantenimientosDto> buscaFirmantes(int noPersona);
	public List<MantenimientosDto> existeFirma(int noPersona);
	public int eliminarFirmante(int noPersona);
	public int modificarFirmante(int noPersona, String nombre, String pathFirma);
	public int insertarFirmante(int noPersona, String nombre, String pathFirma);
	
	//Patanlla de Mantenimiento de Firmas
	public List<LlenaComboGralDto> llenaComboBancos();
	public List<LlenaComboGralDto> llenaComboChequeras(int idBanco);
	public List<LlenaComboGralDto> llenaComboPersonas(String idBanco , String cuenta, boolean busqueda);
	public List<MantenimientosDto> buscaFirmas(String idBanco, String idChequera, String noFirma);
	public String eliminarFirma(MantenimientosDto dto);
	public String insetarFirma(MantenimientosDto dto);
}
