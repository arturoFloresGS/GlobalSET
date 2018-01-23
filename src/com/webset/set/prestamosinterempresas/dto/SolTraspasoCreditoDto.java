package com.webset.set.prestamosinterempresas.dto;

public class SolTraspasoCreditoDto {
	
	private int empresaOrigen;
	private int empresaDestino;
	private int bancoOrigen;
	private int bancoDestino;
	private int idTipoOperacion;
	private double monto;
	private String cheqOrigen;
	private String cheqDestino;
	private String concepto;
	
	
	
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public int getEmpresaOrigen() {
		return empresaOrigen;
	}
	public void setEmpresaOrigen(int empresaOrigen) {
		this.empresaOrigen = empresaOrigen;
	}
	public int getEmpresaDestino() {
		return empresaDestino;
	}
	public void setEmpresaDestino(int empresaDestino) {
		this.empresaDestino = empresaDestino;
	}
	public int getBancoOrigen() {
		return bancoOrigen;
	}
	public void setBancoOrigen(int bancoOrigen) {
		this.bancoOrigen = bancoOrigen;
	}
	public int getBancoDestino() {
		return bancoDestino;
	}
	public void setBancoDestino(int bancoDestino) {
		this.bancoDestino = bancoDestino;
	}
	public String getCheqOrigen() {
		return cheqOrigen;
	}
	public void setCheqOrigen(String cheqOrigen) {
		this.cheqOrigen = cheqOrigen;
	}
	public String getCheqDestino() {
		return cheqDestino;
	}
	public void setCheqDestino(String cheqDestino) {
		this.cheqDestino = cheqDestino;
	}
	public double getMonto() {
		return monto;
	}
	public void setMonto(double monto) {
		this.monto = monto;
	}
	public int getIdTipoOperacion() {
		return idTipoOperacion;
	}
	public void setIdTipoOperacion(int idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}

}
