package com.webset.set.financiamiento.dto;

public class AvalGarantiaCreditoAEDto {
	private int noEmpresaAvalista;
	private String clave;
	private int noEmpresaAvalada;
	private float montoAvalado;
	private String fecAlta;
	private int usuarioAlta;
	private char estatus;
	private int usuarioModif;
	private String fecModif;
	public int getNoEmpresaAvalista() {
		return noEmpresaAvalista;
	}
	public void setNoEmpresaAvalista(int noEmpresaAvalista) {
		this.noEmpresaAvalista = noEmpresaAvalista;
	}
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public int getNoEmpresaAvalada() {
		return noEmpresaAvalada;
	}
	public void setNoEmpresaAvalada(int noEmpresaAvalada) {
		this.noEmpresaAvalada = noEmpresaAvalada;
	}
	public float getMontoAvalado() {
		return montoAvalado;
	}
	public void setMontoAvalado(float montoAvalado) {
		this.montoAvalado = montoAvalado;
	}
	public String getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(String fecAlta) {
		this.fecAlta = fecAlta;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public char getEstatus() {
		return estatus;
	}
	public void setEstatus(char estatus) {
		this.estatus = estatus;
	}
	public int getUsuarioModif() {
		return usuarioModif;
	}
	public void setUsuarioModif(int usuarioModif) {
		this.usuarioModif = usuarioModif;
	}
	public String getFecModif() {
		return fecModif;
	}
	public void setFecModif(String fecModif) {
		this.fecModif = fecModif;
	}
	
}
