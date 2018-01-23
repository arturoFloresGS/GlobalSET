package com.webset.set.bancaelectronica.dao;

import java.util.List;
import java.util.Map;

import com.webset.set.bancaelectronica.dto.EmpresaDto;
import com.webset.set.bancaelectronica.dto.MovtoBancaEDto;

public interface BancaBuzonesDao {

	public String consultarConfiguraSet(int indice);

	public List<String> armaCadenaConexion();
	public List<EmpresaDto> buscarEmpresa(String chequera, int idBanco, boolean swiftMT940_MN);

	public Map<String, Object> selectMD(String folioBanco, String concepto, String referencia, int noEmpresa, String chequera, String fecha,
			double importe, String descripcion);

	public Map<String, Object> selectMovtoBancaE(String folioBanco, String concepto, String referencia, int noEmpresa,
			String chequera, String fecha, double importe, double saldo, long noLineaArchivo, String cargoAbono,
			int idBanco);

	public Map<String, Object> updateFolioReal(String idFolio);

	public Map<String, Object> selectFolioReal(String idFolio);

	public Map<String, Object> insertaBancaMD(int noEmpresa, int santander, String chequera, long folio, String fecha,
			String sucursal, String folioBanco, String referencia, String cargoAbono, double importe, String sbCobro,
			String concepto, int idUsuario, String descripcion, String archivo, String cambiarFecha, String observacion,
			String cveConcepto, double saldo, String estatus, int noLineaArchivo);

	public Map<String, Object> insertaMovtoBancaE(int noEmpresa, int santander, String chequera, long folio,
			String fecha, String sucursal, String folioBanco, String referencia, String cargoAbono, double importe,
			String sbCobro, String concepto, int idUsuario, String descripcion, String archivo, String cambiarFecha,
			String observacion, String cveConcepto, double saldo, String estatus, int noLineaArchivo);

	public Map<String, Object> updateCatCtaBanco(String fecha, String archivos, int idBanco);

	public Map<String, Object> ejecutaConfOperaciones();

	public double llamaProcConfirmaTrans();

	public String selectControlStored(String nombreStored, int idStored);

	public Map<String, Object> ejecutaClasificadorMovtos(String fechaHoy);

	public Map<String, Object> ejecutaBuzonesMovimiento();

	public Map<String, Object> ejecutaConciliaSbcDev();

	public Map<String, Object> ejecutaDatosDevolucionCheque();

	public Map<String, Object> ejecutaFactorajeBancomer();

	public Map<String, Object> ejecutaStoreSPC(int idUsuario);

	public Map<String, Object> verificaSaldoBanco(int idBanco);

	/*	SE SUSTITUYE POR INSERTBATCH().
	 * public String insertaMovtoBancaEStr(int noEmpresa, int idBanco, String chequera, long folio, String fecha,
			String sucursal, String folioBanco, String referencia, String cargoAbono, double importe, String sbCobro,
			String concepto, int idUsuario, String descripcion, String archivo, String fechaHoy, String observacion,
			String cveConcepto, double saldo, String estatus, int contador, String hora, String fechaOperacion, String estatus1);
	 */
	
	public Map<String, Object> ejecutaInsertsMovtoBancaE(List<MovtoBancaEDto> lMovimientos);

	public Map<String, Object> selectMovtosBancomer(String archivo, int idBanco, String chequera);

	public int updateSaldoBancomer(double saldoChequera, String archivo, int idBanco, String chequera, long secuencia);

	public int updateSaldosCatCtaBanco(double saldoChequera, int noEmpresa, int idBanco, String chequera,
			String fechaHoy);

	public int insertaArchMovtoBancaE(String archivoOriginal, String chequera, String fechaHoy, int idBanco);

	public List<EmpresaDto> buscarEmpresaClabe(String clabe, int idBanco);

	public boolean existeMovDia2(int noEmpresa, int idBanco, String chequera, String fechaValor, String referencia,
			String cargoAbono, double importe, String concepto);

	public int insertaMovtoBancaD(int noEmpresa, int idBanco, String chequera, long folio, String fechaValor,
			String sucursal, String folioBanco, String referencia, String cargoAbono, double importe, String concepto,
			int idUsuario, String descripcion, String archivo, int lineaArch, double saldoChequera, String cveConcepto, String fechaHoy);

	public Map<String, Object> ejecutaInsertsMovtoBancaD(List<MovtoBancaEDto> lMovimientos);

	public boolean existeMovBancaE(int noEmpresa, int azteca, String chequera, String fechaValor, String referencia,
			String cargoAbono, double importe, String concepto);

}
