package com.webset.set.barridosfondeos.dto;

/**
 * objeto DTO con la información para el manejo de los datos de traspaso de fondeos/barridos
 * @author COINSIDE
 *
 */
public class TmpTraspasoFondeoDto {
	
	private int idEmpresaOrigen;
	private int idEmpresaDestino;
	private double importeTraspaso;
	private int tipoArbol;
	private int nivel;
	private int idBanco;
	private String idChequeraPadre;
	private String idChequeraHijo;
	private int idUsuario;
	private int tipoOperacion;
	private String concepto;
	private int noFondeo;
	
	/**
	 * @return the idEmpresaOrigen
	 */
	public int getIdEmpresaOrigen() {
		return idEmpresaOrigen;
	}
	/**
	 * @param idEmpresaOrigen the idEmpresaOrigen to set
	 */
	public void setIdEmpresaOrigen(int idEmpresaOrigen) {
		this.idEmpresaOrigen = idEmpresaOrigen;
	}
	/**
	 * @return the idEmpresaDestino
	 */
	public int getIdEmpresaDestino() {
		return idEmpresaDestino;
	}
	/**
	 * @param idEmpresaDestino the idEmpresaDestino to set
	 */
	public void setIdEmpresaDestino(int idEmpresaDestino) {
		this.idEmpresaDestino = idEmpresaDestino;
	}
	/**
	 * @return the importeTraspaso
	 */
	public double getImporteTraspaso() {
		return importeTraspaso;
	}
	/**
	 * @param importeTraspaso the importeTraspaso to set
	 */
	public void setImporteTraspaso(double importeTraspaso) {
		this.importeTraspaso = importeTraspaso;
	}
	/**
	 * @return the tipoArbol
	 */
	public int getTipoArbol() {
		return tipoArbol;
	}
	/**
	 * @param tipoArbol the tipoArbol to set
	 */
	public void setTipoArbol(int tipoArbol) {
		this.tipoArbol = tipoArbol;
	}
	/**
	 * @return the nivel
	 */
	public int getNivel() {
		return nivel;
	}
	/**
	 * @param nivel the nivel to set
	 */
	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
	/**
	 * @return the idBanco
	 */
	public int getIdBanco() {
		return idBanco;
	}
	/**
	 * @param idBanco the idBanco to set
	 */
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	/**
	 * @return the idChequeraPadre
	 */
	public String getIdChequeraPadre() {
		return idChequeraPadre;
	}
	/**
	 * @param idChequeraPadre the idChequeraPadre to set
	 */
	public void setIdChequeraPadre(String idChequeraPadre) {
		this.idChequeraPadre = idChequeraPadre;
	}
	/**
	 * @return the idChequeraHijo
	 */
	public String getIdChequeraHijo() {
		return idChequeraHijo;
	}
	/**
	 * @param idChequeraHijo the idChequeraHijo to set
	 */
	public void setIdChequeraHijo(String idChequeraHijo) {
		this.idChequeraHijo = idChequeraHijo;
	}
	/**
	 * @return the idUsuario
	 */
	public int getIdUsuario() {
		return idUsuario;
	}
	/**
	 * @param idUsuario the idUsuario to set
	 */
	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}
	/**
	 * @return the tipoOperacion
	 */
	public int getTipoOperacion() {
		return tipoOperacion;
	}
	/**
	 * @param tipoOperacion the tipoOperacion to set
	 */
	public void setTipoOperacion(int tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}
	/**
	 * @return the concepto
	 */
	public String getConcepto() {
		return concepto;
	}
	/**
	 * @param concepto the concepto to set
	 */
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	/**
	 * @return the noFondeo
	 */
	public int getNoFondeo() {
		return noFondeo;
	}
	/**
	 * @param noFondeo the noFondeo to set
	 */
	public void setNoFondeo(int noFondeo) {
		this.noFondeo = noFondeo;
	}
	
}
