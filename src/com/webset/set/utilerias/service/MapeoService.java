package com.webset.set.utilerias.service;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.webset.set.personas.dto.ConsultaPersonasDto;
import com.webset.set.utilerias.dto.MapeoDto;


public interface MapeoService {

	public List<MapeoDto> llenaGrid();

	public List<MapeoDto> llenaPoliza();

	public List<MapeoDto> llenaGrupo(String poliza);

	public List<MapeoDto> llenaRubro(String poliza);

	public List<MapeoDto> llenaBanco();

	public List<MapeoDto> obtenerChequeras(String idBanco, int secuencia);

	public int asignarChequeras(int idBanco, int idChequera, boolean todos);

	public String insertaMapeo(List<Map<String, String>> registroGson, List<Map<String, String>> chequerasGson,String idSecuencia);

	public HSSFWorkbook reporteMapeo();

	public String retornaIdMapeo(MapeoDto objMapeoDto);

	public List<MapeoDto> obtieneDatos(String idPoliza, String idGrupo, String idRubro, String idBanco);

	public List<MapeoDto> obtenerChequerasAV(String idBanco, int secuencia);

	public List<MapeoDto> obtenerChequerasM(String idBanco, String secuencia);

	public String actualizaMapeo(List<Map<String, String>> registroGson, String idSecuencia);

	public List<MapeoDto> verificaRegistro(int secuencia);

	public String inhabilitaPersona(int secuencia);

	public String inhabilitaMapeo(int secuencia);

	public void agregarChequerasAV(int secuencia, String chequeras, String banco);



	public void eliminarChequerasAV(int secuencia, String chequeras, String banco);

	public String eliminaChequera(int secuencia);

	public void agregarTodasM(String idBanco, String secuencia);

	public void quitarTodasM(String secuencia);


	
}
