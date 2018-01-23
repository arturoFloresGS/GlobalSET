package com.webset.set.barridosfondeos.dto;

/**
 * objeto DTO con la información del padre de un nodo en el árbol de fondeo
 * @author COINSIDE
 *
 */
public class PadreFondeoDto {
	
	private int idEmpresaOrigen;
	private int idEmpresaDestino;
	private int idBancoPadre;
	private int idBancoHijo;
	private String idChequeraPadre;
	private String idChequeraHijo;
	private int nivel;
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
	 * @return the idBancoPadre
	 */
	public int getIdBancoPadre() {
		return idBancoPadre;
	}
	/**
	 * @param idBancoPadre the idBancoPadre to set
	 */
	public void setIdBancoPadre(int idBancoPadre) {
		this.idBancoPadre = idBancoPadre;
	}
	/**
	 * @return the idBancoHijo
	 */
	public int getIdBancoHijo() {
		return idBancoHijo;
	}
	/**
	 * @param idBancoHijo the idBancoHijo to set
	 */
	public void setIdBancoHijo(int idBancoHijo) {
		this.idBancoHijo = idBancoHijo;
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
		
}
