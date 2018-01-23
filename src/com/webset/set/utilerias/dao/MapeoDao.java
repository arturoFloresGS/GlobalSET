package com.webset.set.utilerias.dao;

import java.util.*;
import com.webset.set.utilerias.dto.MapeoDto;



public interface MapeoDao {

	public List<MapeoDto> llenaGrid();

	public List<MapeoDto> llenaPoliza();

	public List<MapeoDto> llenaGrupo(String poliza);

	public List<MapeoDto> llenaRubro(String poliza);

	public List<MapeoDto> llenaBanco();

	public List<MapeoDto> obtenerChequeras(String idBanco, int secuencia);

	int asignarChequeras(int idBanco, int idChequera, boolean todos);

	public String insertaMapeo(MapeoDto objMapeoDto,List<Map<String, String>> chequera, String idSecuencia);

	public List<Map<String, String>> reporteMapeo();

	public String retornaIdMapeo(MapeoDto objMapeoDto);

	public List<MapeoDto> obtieneDatos(String idPoliza, String idGrupo, String idRubro, String idBanco);

	public List<MapeoDto> obtenerChequerasAV(String idBanco, int secuencia);

	public List<MapeoDto> obtenerChequerasM(String idBanco, String secuencia);

	public String actualizaMapeo(MapeoDto objMapeoDto, String idSecuencia);

	public List<MapeoDto> verificaRegistro(int secuencia);

	public int inhabilitaMapeo(int secuencia);


	public void agregarChequerasAV(int secuencia, String chequeras, String banco);

	public void eliminarChequerasAV(int secuencia, String chequeras, String banco);

	public int eliminaChequera(int secuencia);

	public void agregarTodasM(String idBanco, String secuencia);

	public void quitarTodasM(String secuencia);

}
