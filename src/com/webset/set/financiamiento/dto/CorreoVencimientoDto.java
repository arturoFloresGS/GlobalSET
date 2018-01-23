package com.webset.set.financiamiento.dto;

public class CorreoVencimientoDto {

	private String email;
	private int gpoEmpresaVenc;
	private int usuario;
	private int periodo;
	private int noEmpresa;
	private String nomEmpresa;
	
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getGpoEmpresaVenc() {
		return gpoEmpresaVenc;
	}
	public void setGpoEmpresaVenc(int gpoEmpresaVenc) {
		this.gpoEmpresaVenc = gpoEmpresaVenc;
	}
	public int getUsuario() {
		return usuario;
	}
	public void setUsuario(int usuario) {
		this.usuario = usuario;
	}
	public int getPeriodo() {
		return periodo;
	}
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getNomEmpresa() {
		return nomEmpresa;
	}
	public void setNomEmpresa(String nomEmpresa) {
		this.nomEmpresa = nomEmpresa;
	}

	
}
