package com.webset.set.bancaelectronica.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.ArchTransferDto;
import com.webset.set.utilerias.dto.MovimientoDto;

public interface ControlArchivosTransferidosADao {
	public String configuraSet(int indice);
	public List<ArchTransferDto> llenaComboArchivos(String fecValor, String bChequeOcurre);	
	public List<ArchTransferDto> llenaGridArchivos(ArchTransferDto objeto);
	public List<MovimientoDto> llenaGridDetalle(String nomArchivo, int idBanco, boolean enviadasHoy, String tipoOperacion);
	public List<ArchTransferDto> llenaGridDetalleCheque(String nomArchivo);
	public String obtieneTipoDeMovto(String nomArchivo);
	public String obtieneReferenciaDet(String tipo);
	public String tipoChequera(String idChequera);
	public String seleccionaReferencia(int noEmpresa, String idChequera, int idBanco);
	public int actualizaEstatusAgrupapdo(String poHeaders, int banco, String chequera,int noEmpresa,String fecha, String estatusMov);
	public int actualizaArchTransfer(double importeTotal, int registrosTotal, String fecHoy, int noUsuario, String nomArch);
	public int actualizaDetArchTransferAgrupado(String nomArchivo, String poHeaders, int banco, String chequera,int noEmpresa,String fecha);
	public int actualizaArchivoRegenerado(String nomArchivo, int noFolioDet);
	public int actualizaArchCancelado(double importeTotal, int registroTotal, String nomArch);
	public Map<String, Object> ejecutarRevividor(Map<String, String> registro, boolean revividor);
	public List<String> armaCadenaConexion();
}
