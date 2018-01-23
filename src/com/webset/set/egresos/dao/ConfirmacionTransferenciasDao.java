package com.webset.set.egresos.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.webset.set.egresos.dto.ConfirmacionTransferenciasDto;
import com.webset.set.utilerias.dto.LlenaComboChequeraDto;
import com.webset.set.utilerias.dto.LlenaComboEmpresasDto;
import com.webset.set.utilerias.dto.LlenaComboGralDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface ConfirmacionTransferenciasDao {
	public List<LlenaComboEmpresasDto> obtenerEmpresas(int idUsuario);
	public List<LlenaComboGralDto> llenaComboBanco(int noEmpresa);
	public List<LlenaComboChequeraDto> llenaComboChequera(int idBanco, int noEmpresa, String idDivisa);
	public List<MovimientoDto> consultarMovimiento(int noEmpresa, int idBanco, String idChequera, int hayBanca, int idUsuario, String idDivisa);
	public List<ConfirmacionTransferenciasDto> fechaAyerHoraActual();
	public List<ConfirmacionTransferenciasDto> buscaCargoExiste(Date pdFecha, int piEmpresa, int piBanco, String psChequera, Double pdimporte, String bandera);
	public Map<String, Object> ejecutarConfirmador(int folioDet, String folioBanco, int secuencia, Date fecValor, int result, String msg);
	public int updateReferenciaFolBco(String referencia, int iFoliosDet, String psFolioBanco, Date FechaHoy);
	public String consultarConfiguraSet(int indice);
	public Date obtenerFechaHoy();
	public int validaConfirmacion();
}
