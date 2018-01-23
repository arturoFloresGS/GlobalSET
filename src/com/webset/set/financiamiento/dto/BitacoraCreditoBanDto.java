package com.webset.set.financiamiento.dto;

public class BitacoraCreditoBanDto {
	private int noEmpresa;
	private String idFinanciamiento;
	private int idDisposicion;
	private String fecAlta;
	private String nota;
	private int usuarioAlta;
	private String idFinanciamientoHijo;
	private String idDisposicionHijo;
	private String usuarioC;
	
	public String getUsuarioC() {
		return usuarioC;
	}
	public void setUsuarioC(String usuarioC) {
		this.usuarioC = usuarioC;
	}
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
	public int getIdDisposicion() {
		return idDisposicion;
	}
	public void setIdDisposicion(int idDisposicion) {
		this.idDisposicion = idDisposicion;
	}
	public String getFecAlta() {
		return fecAlta;
	}
	public void setFecAlta(String fecAlta) {
		this.fecAlta = fecAlta;
	}
	public String getNota() {
		return nota;
	}
	public void setNota(String nota) {
		this.nota = nota;
	}
	public int getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(int usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public String getIdFinanciamientoHijo() {
		return idFinanciamientoHijo;
	}
	public void setIdFinanciamientoHijo(String idFinanciamientoHijo) {
		this.idFinanciamientoHijo = idFinanciamientoHijo;
	}
	public String getIdDisposicionHijo() {
		return idDisposicionHijo;
	}
	public void setIdDisposicionHijo(String idDisposicionHijo) {
		this.idDisposicionHijo = idDisposicionHijo;
	}
}
