package com.webset.set.bancaelectronica.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.ArchTransferDto;

public interface ControlArchivosTransferidosDao {
	public String configuraSet(int indice);
	public List<ArchTransferDto> llenaComboArchivos(String fecValor, String bChequeOcurre);	
	public List<ArchTransferDto> llenaGridArchivos(ArchTransferDto objeto);
	public List<ArchTransferDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String tipoOperacion);
	public List<ArchTransferDto> llenaGridDetalleCheque(String nomArchivo);
	public String obtieneTipoDeMovto(String nomArchivo);
	public String obtieneReferenciaDet(String tipo);
	public String tipoChequera(String idChequera);
	public String seleccionaReferencia(int noEmpresa, String idChequera, int idBanco);
	public int actualizaEstatus(int noFolioDet, String estatusMov);
	public int actualizaArchTransfer(double importeTotal, int registrosTotal, String fecHoy, int noUsuario, String nomArch);
	public int actualizaDetArchTransfer(String nomArchivo, int noFolioDet);
	public int actualizaArchivoRegenerado(String nomArchivo, int noFolioDet);
	public int actualizaArchCancelado(double importeTotal, int registroTotal, String nomArch);
	public Map<String, Object> ejecutarRevividor(Map<String, String> registro, boolean revividor);	
}
