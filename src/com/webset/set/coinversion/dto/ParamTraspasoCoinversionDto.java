package com.webset.set.coinversion.dto;

public class ParamTraspasoCoinversionDto {
	
	private int idEmpresaOrigen;
	private int idBancoOrigen;
	private int idEmpresaDestino;
	private int idBancoDestino;
	private int TipoSaldo;
	private double montoTraspaso;
	private double credito;
	private String descEmpresaOrigen;
	private String idChequeraOrigen;
	private String descEmpresaDestino;
	private String idChequeraDestino;
	private String idDivisa;
	private String concepto;
	
	
	public int getTipoSaldo() {
		return TipoSaldo;
	}
	public void setTipoSaldo(int tipoSaldo) {
		TipoSaldo = tipoSaldo;
	}
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public int getIdEmpresaOrigen() {
		return idEmpresaOrigen;
	}
	public void setIdEmpresaOrigen(int idEmpresaOrigen) {
		this.idEmpresaOrigen = idEmpresaOrigen;
	}
	public int getIdBancoOrigen() {
		return idBancoOrigen;
	}
	public void setIdBancoOrigen(int idBancoOrigen) {
		this.idBancoOrigen = idBancoOrigen;
	}
	public int getIdEmpresaDestino() {
		return idEmpresaDestino;
	}
	public void setIdEmpresaDestino(int idEmpresaDestino) {
		this.idEmpresaDestino = idEmpresaDestino;
	}
	public int getIdBancoDestino() {
		return idBancoDestino;
	}
	public void setIdBancoDestino(int idBancoDestino) {
		this.idBancoDestino = idBancoDestino;
	}
	public double getMontoTraspaso() {
		return montoTraspaso;
	}
	public void setMontoTraspaso(double montoTraspaso) {
		this.montoTraspaso = montoTraspaso;
	}
	public double getCredito() {
		return credito;
	}
	public void setCredito(double credito) {
		this.credito = credito;
	}
	public String getDescEmpresaOrigen() {
		return descEmpresaOrigen;
	}
	public void setDescEmpresaOrigen(String descEmpresaOrigen) {
		this.descEmpresaOrigen = descEmpresaOrigen;
	}
	public String getIdChequeraOrigen() {
		return idChequeraOrigen;
	}
	public void setIdChequeraOrigen(String idChequeraOrigen) {
		this.idChequeraOrigen = idChequeraOrigen;
	}
	public String getDescEmpresaDestino() {
		return descEmpresaDestino;
	}
	public void setDescEmpresaDestino(String descEmpresaDestino) {
		this.descEmpresaDestino = descEmpresaDestino;
	}
	public String getIdChequeraDestino() {
		return idChequeraDestino;
	}
	public void setIdChequeraDestino(String idChequeraDestino) {
		this.idChequeraDestino = idChequeraDestino;
	}
	public String getIdDivisa() {
		return idDivisa;
	}
	public void setIdDivisa(String idDivisa) {
		this.idDivisa = idDivisa;
	}
}
