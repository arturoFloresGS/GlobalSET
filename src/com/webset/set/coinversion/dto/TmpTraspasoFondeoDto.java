package com.webset.set.coinversion.dto;

public class TmpTraspasoFondeoDto {
	
	private int idEmpresaOrigen;
	private int idEmpresaDestino;
	double importeTraspaso;
	private int tipoArbol;
	private int orden;
	private int idBanco;
	private String idChequeraHijo;
	private String idChequeraPadre;
	
	public int getIdEmpresaOrigen() {
		return idEmpresaOrigen;
	}
	public void setIdEmpresaOrigen(int idEmpresaOrigen) {
		this.idEmpresaOrigen = idEmpresaOrigen;
	}
	public int getIdEmpresaDestino() {
		return idEmpresaDestino;
	}
	public void setIdEmpresaDestino(int idEmpresaDestino) {
		this.idEmpresaDestino = idEmpresaDestino;
	}
	public double getImporteTraspaso() {
		return importeTraspaso;
	}
	public void setImporteTraspaso(double importeTraspaso) {
		this.importeTraspaso = importeTraspaso;
	}
	public int getTipoArbol() {
		return tipoArbol;
	}
	public void setTipoArbol(int tipoArbol) {
		this.tipoArbol = tipoArbol;
	}
	public int getOrden() {
		return orden;
	}
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public int getIdBanco() {
		return idBanco;
	}
	public void setIdBanco(int idBanco) {
		this.idBanco = idBanco;
	}
	public String getIdChequeraHijo() {
		return idChequeraHijo;
	}
	public void setIdChequeraHijo(String idChequeraHijo) {
		this.idChequeraHijo = idChequeraHijo;
	}
	public String getIdChequeraPadre() {
		return idChequeraPadre;
	}
	public void setIdChequeraPadre(String idChequeraPadre) {
		this.idChequeraPadre = idChequeraPadre;
	}

}
