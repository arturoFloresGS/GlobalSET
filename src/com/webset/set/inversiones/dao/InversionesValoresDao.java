package com.webset.set.inversiones.dao;
import java.util.List;
import com.webset.set.inversiones.dto.MantenimientoValoresDto;
import com.webset.set.inversiones.dto.LlenaComboValoresDto;

public interface InversionesValoresDao {
	public List<MantenimientoValoresDto> consultarValores();
	public List<LlenaComboValoresDto> consultarDivisa();
    public int insertarValores(List<MantenimientoValoresDto> dtoInsVal);
    public int modificarValores(List<MantenimientoValoresDto> dtoInsVal);
	public int eliminarValores(String sIdValor);
	public int existeValorAsociadoAPapel( String idValor );
	public int  buscaValores(List<MantenimientoValoresDto> listValores);
}
