package com.webset.set.bancaelectronica.dto;
/**
 * 
 * @author Sergio Vaca
 * @since 01/Noviembre/2010
 */
/**
 * Clase que contiene los parametros de busqueda de los layout
 */
public class ParametroLayoutDto {
	private String psArchivos;
	private String psChequerasInexistentes;
	private int plRegLeidos;
	private int plRegSinChequera;
	private int plRegImportados;
	
	/*para busqueda movto_banca_e*/
	private String psFolioBanco;
	private String psConcepto;
	private String psReferencia;
	private String psIdChequera;
	private String psFecValor;
	private String cargoAbono;
	private int piBanco;
	private long plNoLineaArchivo;
	private long plNoEmpresa;
	private double pdImporte;
	private double pdSaldo;
	
	//agredados para la insercion
	private long psSecuencia;
	private double pdSaldoBancario;
	private String psBSalvoBuenCobro;
	private String psFecAlta;
	private String psCargoAbono;
	private int psUsuarioAlta;
	private int contadorRenglon;
	private String psDescripcion;
	private String psArchivo;
	private String psSucursal;
	private String psCveConcepto;
	
	private String psEstatus;
	private String psObservacion;
	private String psFechaHoy;
	private String psHora;
	
	//Para Consultas
	private String psRegistro;
	private int folioIni;
	private int folioLong;
	
	public String getPsObservacion() {
		return psObservacion;
	}
	public void setPsObservacion(String psObservacion) {
		this.psObservacion = psObservacion;
	}
	public String getPsEstatus() {
		return psEstatus;
	}
	public void setPsEstatus(String psEstatus) {
		this.psEstatus = psEstatus;
	}
	public double getPdSaldoBancario(){
		return pdSaldoBancario;
	}
	public void setPdSaldoBancario(double pdSaldoBancario) {
		this.pdSaldoBancario = pdSaldoBancario;
	}
	public String getPsArchivos() {
		return psArchivos;
	}
	public void setPsArchivos(String psArchivos) {
		this.psArchivos = psArchivos;
	}
	public String getPsChequerasInexistentes() {
		return psChequerasInexistentes;
	}
	public void setPsChequerasInexistentes(String psChequerasInexistentes) {
		this.psChequerasInexistentes = psChequerasInexistentes;
	}
	public int getPlRegLeidos() {
		return plRegLeidos;
	}
	public void setPlRegLeidos(int plRegLeidos) {
		this.plRegLeidos = plRegLeidos;
	}
	public int getPlRegSinChequera() {
		return plRegSinChequera;
	}
	public void setPlRegSinChequera(int plRegSinChequera) {
		this.plRegSinChequera = plRegSinChequera;
	}
	public int getPlRegImportados() {
		return plRegImportados;
	}
	public void setPlRegImportados(int plRegImportados) {
		this.plRegImportados = plRegImportados;
	}
	public String getPsFolioBanco() {
		return psFolioBanco;
	}
	public void setPsFolioBanco(String psFolioBanco) {
		this.psFolioBanco = psFolioBanco;
	}
	public String getPsConcepto() {
		return psConcepto;
	}
	public void setPsConcepto(String psConcepto) {
		this.psConcepto = psConcepto;
	}
	public String getPsReferencia() {
		return psReferencia;
	}
	public void setPsReferencia(String psReferencia) {
		this.psReferencia = psReferencia;
	}
	public String getPsIdChequera() {
		return psIdChequera;
	}
	public void setPsIdChequera(String psIdChequera) {
		this.psIdChequera = psIdChequera;
	}
	public String getPsFecValor() {
		return psFecValor;
	}
	public void setPsFecValor(String psFecValor) {
		this.psFecValor = psFecValor;
	}
	public String getCargoAbono() {
		return cargoAbono;
	}
	public void setCargoAbono(String cargoAbono) {
		this.cargoAbono = cargoAbono;
	}
	public int getPiBanco() {
		return piBanco;
	}
	public void setPiBanco(int piBanco) {
		this.piBanco = piBanco;
	}
	public long getPlNoLineaArchivo() {
		return plNoLineaArchivo;
	}
	public void setPlNoLineaArchivo(long plNoLineaArchivo) {
		this.plNoLineaArchivo = plNoLineaArchivo;
	}
	public long getPlNoEmpresa() {
		return plNoEmpresa;
	}
	public void setPlNoEmpresa(long plNoEmpresa) {
		this.plNoEmpresa = plNoEmpresa;
	}
	public double getPdImporte() {
		return pdImporte;
	}
	public void setPdImporte(double pdImporte) {
		this.pdImporte = pdImporte;
	}
	public double getPdSaldo() {
		return pdSaldo;
	}
	public void setPdSaldo(double pdSaldo) {
		this.pdSaldo = pdSaldo;
	}
	public long getPsSecuencia() {
		return psSecuencia;
	}
	public void setPsSecuencia(long psSecuencia) {
		this.psSecuencia = psSecuencia;
	}
	public String getPsBSalvoBuenCobro() {
		return psBSalvoBuenCobro;
	}
	public void setPsBSalvoBuenCobro(String psBSalvoBuenCobro) {
		this.psBSalvoBuenCobro = psBSalvoBuenCobro;
	}
	public String getPsFecAlta() {
		return psFecAlta;
	}
	public void setPsFecAlta(String psFecAlta) {
		this.psFecAlta = psFecAlta;
	}
	public String getPsCargoAbono() {
		return psCargoAbono;
	}
	public void setPsCargoAbono(String psCargoAbono) {
		this.psCargoAbono = psCargoAbono;
	}
	public int getPsUsuarioAlta() {
		return psUsuarioAlta;
	}
	public void setPsUsuarioAlta(int psUsuarioAlta) {
		this.psUsuarioAlta = psUsuarioAlta;
	}
	public String getPsDescripcion() {
		return psDescripcion;
	}
	public void setPsDescripcion(String psDescripcion) {
		this.psDescripcion = psDescripcion;
	}
	public String getPsArchivo() {
		return psArchivo;
	}
	public void setPsArchivo(String psArchivo) {
		this.psArchivo = psArchivo;
	}
	public String getPsSucursal() {
		return psSucursal;
	}
	public void setPsSucursal(String psSucursal) {
		this.psSucursal = psSucursal;
	}
	public String getPsCveConcepto() {
		return psCveConcepto;
	}
	public void setPsCveConcepto(String psCveConcepto) {
		this.psCveConcepto = psCveConcepto;
	}
	public int getContadorRenglon() {
		return contadorRenglon;
	}
	public void setContadorRenglon(int contadorRenglon) {
		this.contadorRenglon = contadorRenglon;
	}
	public String getPsFechaHoy() {
		return psFechaHoy;
	}
	public void setPsFechaHoy(String psFechaHoy) {
		this.psFechaHoy = psFechaHoy;
	}
	public String getPsHora() {
		return psHora;
	}
	public void setPsHora(String psHora) {
		this.psHora = psHora;
	}
	public int getFolioIni() {
		return folioIni;
	}
	public void setFolioIni(int folioIni) {
		this.folioIni = folioIni;
	}
	public int getFolioLong() {
		return folioLong;
	}
	public void setFolioLong(int folioLong) {
		this.folioLong = folioLong;
	}
	public String getPsRegistro() {
		return psRegistro;
	}
	public void setPsRegistro(String psRegistro) {
		this.psRegistro = psRegistro;
	}
	

}
