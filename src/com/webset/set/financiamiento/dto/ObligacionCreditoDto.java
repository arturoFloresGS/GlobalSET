package com.webset.set.financiamiento.dto;

public class ObligacionCreditoDto {
	private int noEmpresa;
	private String idFinanciamiento;
	private int idClave;
	private String descripcion;
	private String fecAlta;
	private int noUsuario;
	private char estatus;
	private int usuarioModif;
	private String fecModif;
	public int getNoEmpresa() {
		return noEmpresa;
	}
	public void setNoEmpresa(int noEmpresa) {
		this.noEmpresa = noEmpresa;
	}
	public String getIdFinanciamiento() {
		return idFinanciamiento;
	}
	public void setIdFinanciamiento(String idFinanciamiento) {
		this.idFinanciamiento = idFinanciamiento;
	}
	public int getIdClave() {
		return idClave;
	}
	public void setIdClave(int idClave) {
		this.idClave = idClave;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(String fecAlta) {
		this.fecAlta = fecAlta;
	}
	public int getNoUsuario() {
		return noUsuario;
	}
	public void setNoUsuario(int noUsuario) {
		this.noUsuario = noUsuario;
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
